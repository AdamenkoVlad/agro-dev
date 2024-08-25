package com.abi.agro_back.controller;

import com.abi.agro_back.auth.AuthenticationResponse;
import com.abi.agro_back.collection.RegisteringRequest;
import com.abi.agro_back.collection.Role;
import com.abi.agro_back.service.RegisteringRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/reg-request")
@Tag(name = "RegisteringRequest", description = "the Registering Request From User Endpoint")
public class RegisteringRequestController {

    @Autowired
    private RegisteringRequestService registeringRequestService;

    @PostMapping
    public ResponseEntity<RegisteringRequest> createRegisteringRequest(@Validated @RequestBody RegisteringRequest registeringRequest) {
        return new ResponseEntity<>(registeringRequestService.createRegisteringRequest(registeringRequest), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<RegisteringRequest> getRegisteringRequestById(@PathVariable("id") String id) {
        return ResponseEntity.ok(registeringRequestService.getRegisteringRequestById(id));
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<AuthenticationResponse> approveRegisteringRequestById(@PathVariable("id") String id, @RequestParam("role")Role role,@RequestBody List<String> permissions) {
        return ResponseEntity.ok(registeringRequestService.approveRegisteringRequestById(id,role,permissions));
    }

    @GetMapping()
    public ResponseEntity<List<RegisteringRequest>> getAllRegisteringRequests() {
        return ResponseEntity.ok(registeringRequestService.getAllRegisteringRequests());
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<RegisteringRequest> updateRegisteringRequest(@PathVariable("id") String  registeringRequestId,
                                              @RequestBody RegisteringRequest updatedRegisteringRequest) {
        return ResponseEntity.ok(registeringRequestService.updateRegisteringRequest(registeringRequestId, updatedRegisteringRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteRegisteringRequestById(@PathVariable("id") String  id) {
        registeringRequestService.deleteRegisteringRequest(id);
        return ResponseEntity.ok("RegisteringRequest deleted successfully!");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
