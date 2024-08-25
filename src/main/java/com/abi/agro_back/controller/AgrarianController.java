package com.abi.agro_back.controller;

import com.abi.agro_back.collection.Agrarian;
import com.abi.agro_back.collection.Note;
import com.abi.agro_back.collection.SortField;
import com.abi.agro_back.collection.User;
import com.abi.agro_back.exception.AccessDeniedException;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.repository.OblastConfigRepository;
import com.abi.agro_back.service.AgrarianService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/api/agrarians")
@Tag(name = "Agrarian", description = "the Agrarian Endpoint")
public class AgrarianController {

    @Autowired
    private AgrarianService agrarianService;

    @Autowired
    OblastConfigRepository oblastConfigRepository;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Agrarian> createAgrarian(@RequestPart(name = "image", required = false) MultipartFile image,
                                                   @Valid @RequestPart Agrarian agrarian) throws IOException {
        return new ResponseEntity<>(agrarianService.createAgrarian(image, agrarian), HttpStatus.CREATED);
    }

    @PutMapping(consumes = {"multipart/form-data"}, value = "{id}")
    public ResponseEntity<Agrarian> updateAgrarian(@PathVariable("id") String agrarianId,
                                                   @RequestPart(name = "image", required = false) MultipartFile image,
                                                   @Valid @RequestPart Agrarian updatedAgrarian) throws IOException {
        Agrarian agrarian = agrarianService.updateAgrarian(agrarianId, image, updatedAgrarian);
        return ResponseEntity.ok(agrarian);
    }

    @PostMapping("/note")
    public ResponseEntity<Note> sendNote(@Valid @RequestBody Note note) {

        return new ResponseEntity<>(agrarianService.sendNote(note), HttpStatus.CREATED);
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<List<Note>> getUserNotesByAgrarianId(@PathVariable("id") String id) {

        return ResponseEntity.ok(agrarianService.getUserNotesByAgrarianId(id));
    }

    @GetMapping("{id}")
    public ResponseEntity<Agrarian> getAgrarianById(@PathVariable("id") String id) {
        Agrarian agrarian = agrarianService.getAgrarianById(id);

        return ResponseEntity.ok(agrarian);
    }

    @GetMapping()
    public ResponseEntity<List<Agrarian>> getAllAgrarians() {
        List<Agrarian> agrarians = agrarianService.getAllAgrarians();
        return ResponseEntity.ok(agrarians);
    }

    @GetMapping("/priority")
    public ResponseEntity<List<Agrarian>> getAllAgrariansByPriority() {
        return ResponseEntity.ok(agrarianService.getAllAgrariansByPriority());
    }

//    @GetMapping("/oblast")
//    public ResponseEntity<List<Agrarian>> getAllAgrariansByOblast(@RequestParam("oblast") String oblast) {
//
//        return ResponseEntity.ok(agrarianService.getAllAgrariansByOblast(oblast));
//    }

    @GetMapping("/region")
    public Page<Agrarian> getAllAgrariansByRegion(@RequestParam("oblast") String oblast,
                                                  @RequestParam("region") String region,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int sizePerPage,
                                                  @RequestParam(defaultValue = "START_DATE") SortField sortField,
                                                  @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
                                                  @RequestParam(required = false) String title,
                                                  @RequestParam(required = false) String services,
                                                  @RequestParam(required = false) String sells,
                                                  @RequestParam(required = false) String head) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                User user = (User) authentication.getPrincipal();
                if (user.getRole().toString().equals("ADMIN")) {
                    return agrarianService.getAllAgrariansByRegion(oblast, region, PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName()), title, services, sells, head);
                }
                if (user.getOblasts().contains(oblast) && user.getEndDate().after(new Date(System.currentTimeMillis()))) {
                    return agrarianService.getAllAgrariansByRegion(oblast, region, PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName()), title, services, sells, head);
                } else {
                    if (oblastConfigRepository.findByOblastAndOldRegion(oblast, region).isPresent()) {
                        return agrarianService.getAllAgrariansByRegion(oblast, region, PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName()), title, services, sells, head);
                    } else {
                        throw new AccessDeniedException("you need to log in or buy access");
                    }
                }
            } else {
                throw new ResourceNotFoundException("User not authenticated");
            }
        } else {
            if (oblastConfigRepository.findByOblastAndOldRegion(oblast, region).isPresent()) {
                return agrarianService.getAllAgrariansByRegion(oblast, region, PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName()), title, services, sells, head);
            } else {
                throw new ResourceNotFoundException("you need to log in");
            }
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAgrarianById(@PathVariable("id") String id) {
        agrarianService.deleteAgrarian(id);
        return ResponseEntity.ok("Agrarian deleted successfully!");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCountAllAgrarians() {
        long count = agrarianService.getCountAllAgrarians();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/obl")
    public ResponseEntity<Long> getCountAgrariansByOblast(@RequestParam("oblast") String oblast) {
        long count = agrarianService.getCountAgrariansByOblast(oblast);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/region")
    public ResponseEntity<Long> getCountAgrariansByRegion(@RequestParam("oblast") String oblast,
                                                          @RequestParam("region") String region) {
        long count = agrarianService.getCountAgrariansByRegion(oblast, region);
        return ResponseEntity.ok(count);
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
