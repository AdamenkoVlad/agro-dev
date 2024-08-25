package com.abi.agro_back.controller;

import com.abi.agro_back.collection.ImagePage;
import com.abi.agro_back.collection.Photo;
import com.abi.agro_back.collection.SortField;
import com.abi.agro_back.config.StorageService;
import com.abi.agro_back.service.ImagePageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/imagePages")
@Tag(name = "ImagePage", description = "the ImagePage Endpoint")
public class ImagePageController {

    @Autowired
    private ImagePageService imagePageService;
    @Autowired
    private StorageService storageService;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ImagePage> createImagePage(@RequestPart(name = "photos", required = false) List<MultipartFile> photos,
                                                     @RequestPart(name = "image", required = false) MultipartFile image,
//                                                     @RequestPart("logo") MultipartFile logo,
                                                     @Valid @RequestPart("imagePage") ImagePage imagePage) throws IOException {

        imagePage.setGalleryPhotos(new ArrayList<>());
        if (photos != null) {
            for (MultipartFile f : photos) {
                String key = f.getOriginalFilename() + "" + System.currentTimeMillis();
                URL url = storageService.uploadPhoto(f, key);
                Photo photo = new Photo(key, url);
                imagePage.getGalleryPhotos().add(photo);
            }
        }

        String imageKey = System.currentTimeMillis() + "" + image.getOriginalFilename();
        URL imageUrl = storageService.uploadPhoto(image, imageKey);
        Photo imagePhoto = new Photo(imageKey, imageUrl);
        imagePage.setImage(imagePhoto);

//        String logoKey = featuredImage.getOriginalFilename() + "" + System.currentTimeMillis();
//        URL logoUrl = storageService.uploadPhoto(logo, logoKey);
//        Photo logoPhoto = new Photo(logoKey, logoUrl);
//        imagePage.setLogo(logoPhoto);


        ImagePage savedImagePage = imagePageService.createImagePage(imagePage);
        return new ResponseEntity<>(savedImagePage, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<ImagePage> getImagePageById(@PathVariable("id") String id) {

        return ResponseEntity.ok(imagePageService.getImagePageById(id));
    }

    @GetMapping()
    public Page<ImagePage> getAllImagePages(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int sizePerPage,
                                            @RequestParam(defaultValue = "TITLE") SortField sortField,
                                            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        Pageable pageable = PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName());
        return imagePageService.getAllImagePages(pageable);
    }

    @GetMapping("/page")
    public Page<ImagePage> findAllByPage(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int sizePerPage,
                                         @RequestParam(defaultValue = "TITLE") SortField sortField,
                                         @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        Pageable pageable = PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName());
        return imagePageService.findAllByPage(pageable);
    }

    @PutMapping(value = "{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<ImagePage> updateImagePage(@PathVariable("id") String  imagePageId,
                                                     @RequestPart(name = "photos", required = false) List<MultipartFile> photos,
                                                     @RequestPart(name = "image", required = false) MultipartFile image,
                                                     @RequestPart ImagePage updatedImagePage) throws IOException {
        return ResponseEntity.ok(imagePageService.updateImagePage(imagePageId, image, photos, updatedImagePage));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteImagePageById(@PathVariable("id") String  id) {
        imagePageService.deleteImagePage(id);
        return ResponseEntity.ok("ImagePage deleted successfully!");
    }

    @GetMapping("/search")
    public ResponseEntity<List<ImagePage>> getImagePagesByKeySearch(@RequestParam String  key) {
        return ResponseEntity.ok(imagePageService.getImagePagesByKeySearch(key));
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
