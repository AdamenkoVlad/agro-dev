package com.abi.agro_back.controller;

import com.abi.agro_back.collection.User;
import com.abi.agro_back.collection.VillageCouncil;
import com.abi.agro_back.exception.AccessDeniedException;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.repository.OblastConfigRepository;
import com.abi.agro_back.service.VillageCouncilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/villageCouncil")
@Tag(name = "Village Council", description = "the Village Council Endpoint")
public class VillageCouncilController {

    @Autowired
    private  VillageCouncilService villageCouncilService;

    @Autowired
    OblastConfigRepository oblastConfigRepository;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<VillageCouncil> createVillageCouncil(@RequestPart(name = "image", required = false) MultipartFile image,
                                                               @Valid @RequestPart VillageCouncil villageCouncil) throws IOException {
        return new ResponseEntity<>(villageCouncilService.createVillageCouncil(image, villageCouncil), HttpStatus.CREATED);
    }

    @PutMapping(consumes = { "multipart/form-data" }, value = "{id}")
    public ResponseEntity<VillageCouncil> updateVillageCouncil(@PathVariable("id") String  villageCouncilId,
                                                               @RequestPart(name = "image", required = false) MultipartFile image,
                                                               @Valid @RequestPart VillageCouncil updatedVillageCouncil) throws IOException {
        VillageCouncil villageCouncil = villageCouncilService.updateVillageCouncil(villageCouncilId, image, updatedVillageCouncil);
        return ResponseEntity.ok(villageCouncil);
    }


    @GetMapping("{id}")
    public ResponseEntity<VillageCouncil> getVillageCouncilById(@PathVariable("id") String id) {
        VillageCouncil villageCouncil = villageCouncilService.getVillageCouncilById(id);
        return ResponseEntity.ok(villageCouncil);
    }

    @GetMapping()
    public ResponseEntity<List<VillageCouncil>> getAllVillageCouncils() {
        List<VillageCouncil> villageCouncils = villageCouncilService.getAllVillageCouncils();
        return ResponseEntity.ok(villageCouncils);
    }

    @GetMapping("/region")
    public ResponseEntity<List<VillageCouncil>> getAllVillageCouncilsByRegion(@RequestParam("oblast") String oblast,
                                                                              @RequestParam("region") String region,
                                                                              @RequestParam(required = false) String title,
                                                                              @RequestParam(required = false) String services,
                                                                              @RequestParam(required = false) String sells,
                                                                              @RequestParam(required = false) String head) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                User user = (User) authentication.getPrincipal();
                if (user.getRole().toString().equals("ADMIN")) {
                    return ResponseEntity.ok(villageCouncilService.getAllVillageCouncilsByRegion(oblast, region, title, services, sells, head));
                }
                if (user.getOblasts().contains(oblast) && user.getEndDate().after(new Date(System.currentTimeMillis()))){
                    return ResponseEntity.ok(villageCouncilService.getAllVillageCouncilsByRegion(oblast, region, title, services, sells, head));
                } else {
                    if (oblastConfigRepository.findByOblastAndOldRegion(oblast, region).isPresent()) {
                        return ResponseEntity.ok(villageCouncilService.getAllVillageCouncilsByRegion(oblast, region, title, services, sells, head));
                    } else {
                        throw new AccessDeniedException("you need to log in");
                    }
                }
            } else {
                throw new ResourceNotFoundException("User not authenticated");
            }
        } else {
            if (oblastConfigRepository.findByOblastAndOldRegion(oblast, region).isPresent()) {
                return ResponseEntity.ok(villageCouncilService.getAllVillageCouncilsByRegion(oblast, region, title, services, sells, head));
            } else {
                throw new ResourceNotFoundException("you need to log in");
            }
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteVillageCouncilById(@PathVariable("id") String  id) {
        villageCouncilService.deleteVillageCouncil(id);
        return ResponseEntity.ok("VillageCouncil deleted successfully!");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleUnavailableForLegalReasonsException(AccessDeniedException ex) {
        return ResponseEntity.status(451).body(ex.getMessage());
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
