package com.abi.agro_back.controller;

import com.abi.agro_back.collection.*;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.repository.DemoRepository;
import com.abi.agro_back.repository.OblastConfigRepository;
import com.abi.agro_back.service.AgrarianService;
import com.abi.agro_back.service.DemoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/oblastConfig")
@Tag(name = "oblastConfig", description = "the oblastConfig Endpoint")
public class OblastConfigController {

    @Autowired
    OblastConfigRepository oblastConfigRepository;

    @PostMapping("/set")
    public ResponseEntity<OblastConfig> setConfig(@RequestBody OblastConfig oblastConfig) {
        oblastConfigRepository.findByOblastAndOldRegion(oblastConfig.getOblast(), oblastConfig.getOldRegion()).ifPresent(oblastConfig1 ->
                oblastConfigRepository.deleteById(oblastConfig1.getId()));

        return new ResponseEntity<>(oblastConfigRepository.save(oblastConfig), HttpStatus.CREATED);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteConfig(@RequestBody OblastConfig oblastConfig) {
        oblastConfigRepository.findByOblastAndOldRegion(oblastConfig.getOblast(), oblastConfig.getOldRegion()).ifPresent(oblastConfig1 ->
                oblastConfigRepository.deleteById(oblastConfig1.getId()));

        return ResponseEntity.ok("deleted");
    }

    @GetMapping("/all/{oblast}")
    public ResponseEntity<List<String>> getAllOblastConfigRegions(@PathVariable String oblast) {
        List<String> regions = new ArrayList<>();
        for (OblastConfig oblastConfig : oblastConfigRepository.findByOblast(oblast)){
            regions.add(oblastConfig.getOldRegion());
        }
        return ResponseEntity.ok(regions);
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
