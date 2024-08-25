package com.abi.agro_back.controller;

import com.abi.agro_back.collection.ServiceRequest;
import com.abi.agro_back.service.ServiceRequestService;
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
@RequestMapping("/api/service-request")
@Tag(name = "ServiceRequest", description = "the Service Request From User Endpoint")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @PostMapping
    public ResponseEntity<String> createServiceRequest(@Validated @RequestBody ServiceRequest serviceRequest) {
        serviceRequestService.createServiceRequest(serviceRequest);
        return ResponseEntity.ok("Request created");
    }

    @GetMapping("{id}")
    public ResponseEntity<ServiceRequest> getServiceRequestById(@PathVariable("id") String id) {
        return ResponseEntity.ok(serviceRequestService.getServiceRequestById(id));
    }

    @GetMapping()
    public ResponseEntity<List<ServiceRequest>> getAllServiceRequests() {
        return ResponseEntity.ok(serviceRequestService.getAllServiceRequests());
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<ServiceRequest> updateServiceRequest(@PathVariable("id") String  serviceRequestId,
                                              @RequestBody ServiceRequest updatedServiceRequest) {
        return ResponseEntity.ok(serviceRequestService.updateServiceRequest(serviceRequestId, updatedServiceRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteServiceRequestById(@PathVariable("id") String  id) {
        serviceRequestService.deleteServiceRequest(id);
        return ResponseEntity.ok("ServiceRequest deleted successfully!");
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
