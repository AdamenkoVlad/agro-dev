package com.abi.agro_back.controller;

import com.abi.agro_back.collection.Page;
import com.abi.agro_back.collection.PageDto;
import com.abi.agro_back.collection.Photo;
import com.abi.agro_back.collection.SortField;
import com.abi.agro_back.config.StorageService;
import com.abi.agro_back.service.PageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/pages")
@Tag(name = "Page", description = "the Page Endpoint")
public class PageController {

    @Autowired
    private PageService pageService;
    @Autowired
    private StorageService storageService;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<Page> createPage(@RequestPart(name = "image", required = false) MultipartFile image,
                                              @Valid @RequestPart("page") PageDto page) throws IOException {
        if (image != null){
            String imageKey = System.currentTimeMillis() + "" + image.getOriginalFilename();
            URL imageUrl = storageService.uploadPhoto(image, imageKey);
            Photo imagePhoto = new Photo(imageKey, imageUrl);
            page.setImage(imagePhoto);
            return new ResponseEntity<>(pageService.createPage(page), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(pageService.createPage(page), HttpStatus.CREATED);
        }

    }
    @PatchMapping("/admin/approve/{id}")
    public ResponseEntity<?> approveEntity(@PathVariable String id) {
        Page page = pageService.adminApprovesPage(id);
        return ResponseEntity.ok(page);
    }

    @GetMapping("{id}")
    public ResponseEntity<Page> getPageById(@PathVariable("id") String id) {
        return ResponseEntity.ok(pageService.getPageById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Page>> getAllPages() {
        return ResponseEntity.ok(pageService.getAllPages());
    }

    @GetMapping("/forApprove")
    public ResponseEntity<List<Page>> getAllForApprovePages() {
        return ResponseEntity.ok(pageService.getAllForApprovePages());
    }

    @GetMapping("/page")
    public org.springframework.data.domain.Page<Page> findAllByPage(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "20") int sizePerPage,
                                                                          @RequestParam(defaultValue = "START_DATE") SortField sortField,
                                                                          @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        return pageService.findAllByPage(PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName()));
    }
    @PutMapping(value = "{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<Page> updatePage(@PathVariable("id") String  pageId,
                                           @RequestPart(name = "image", required = false) MultipartFile image,
                                           @RequestPart Page updatedPage) throws IOException {
        return ResponseEntity.ok(pageService.updatePage(pageId, image, updatedPage));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePageById(@PathVariable("id") String  id) {
        pageService.deletePage(id);
        return ResponseEntity.ok("Page deleted successfully!");
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
