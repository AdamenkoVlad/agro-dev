package com.abi.agro_back.controller;

import com.abi.agro_back.collection.Exhibition;
import com.abi.agro_back.collection.Photo;
import com.abi.agro_back.collection.SortField;
import com.abi.agro_back.config.StorageService;
import com.abi.agro_back.service.ExhibitionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/exhibitions")
@Tag(name = "Exhibition", description = "the Exhibition Endpoint")
public class ExhibitionController {

    @Autowired
    private ExhibitionService exhibitionService;

    @Autowired
    private StorageService storageService;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<Exhibition> createExhibition(@RequestPart(name = "photos", required = false) List<MultipartFile> photos,
                                                       @RequestPart(name = "image", required = false) MultipartFile image,
                                                       @Valid @RequestPart("exhibition") Exhibition exhibition) throws IOException {
        exhibition.setGalleryPhotos(new ArrayList<>());
        if (photos != null) {
            for (MultipartFile f : photos) {
                String key = f.getOriginalFilename() + "" + System.currentTimeMillis();
                URL url = storageService.uploadPhoto(f, key);
                Photo photo = new Photo(key, url);
                exhibition.getGalleryPhotos().add(photo);
            }
        }

        String imageKey = System.currentTimeMillis() + "" + image.getOriginalFilename();
        URL imageUrl = storageService.uploadPhoto(image, imageKey);
        Photo imagePhoto = new Photo(imageKey, imageUrl);
        exhibition.setImage(imagePhoto);

        Exhibition savedExhibition = exhibitionService.createExhibition(exhibition);
        return new ResponseEntity<>(savedExhibition, HttpStatus.CREATED);
    }

//    @PostMapping(path = "/photo/image/{id}", consumes = { "multipart/form-data" })
//    public ResponseEntity<Exhibition> updateImageById(@RequestPart("image") MultipartFile image,
//                                                             @PathVariable("id") String id) throws IOException {
//        Exhibition exhibition = exhibitionService.getExhibitionById(id);
//        storageService.deletePhoto(exhibition.getImage().getKey());
//
//        String key = image.getOriginalFilename() + "" + System.currentTimeMillis();
//        URL url = storageService.uploadPhoto(image, key);
//        Photo photo = new Photo(key, url);
//        exhibition.setImage(photo);
//
//        Exhibition savedExhibition = exhibitionService.createExhibition(exhibition);
//        return new ResponseEntity<>(savedExhibition, HttpStatus.CREATED);
//    }


//    @PostMapping(path = "/photo/logo/{id}", consumes = { "multipart/form-data" })
//    public ResponseEntity<Exhibition> updateLogoById(@RequestPart("logo") MultipartFile logo,
//                                                      @PathVariable("id") String id) throws IOException {
//        Exhibition exhibition = exhibitionService.getExhibitionById(id);
//
//        String key = logo.getOriginalFilename() + "" + System.currentTimeMillis();
//        URL url = storageService.uploadPhoto(logo, key);
//        Photo photo = new Photo(key, url);
//        exhibition.setLogo(photo);
//
//        Exhibition savedExhibition = exhibitionService.createExhibition(exhibition);
//        storageService.deletePhoto(exhibition.getLogo().getKey());
//
//        return new ResponseEntity<>(savedExhibition, HttpStatus.CREATED);
//    }

//    @PostMapping(path = "/photo/gallery/{id}", consumes = { "multipart/form-data" })
//    public ResponseEntity<Exhibition> addPhotoToGalleryById(@RequestPart("photos") List<MultipartFile> photos,
//                                                    @PathVariable("id") String id) throws IOException {
//        Exhibition exhibition = exhibitionService.getExhibitionById(id);
//        for (MultipartFile f : photos) {
//            String key = f.getOriginalFilename() + "" + System.currentTimeMillis();
//            URL url = storageService.uploadPhoto(f, key);
//            Photo photo = new Photo(key, url);
//            exhibition.getGallery_photos().add(photo);
//        }
//
//        Exhibition savedExhibition = exhibitionService.createExhibition(exhibition);
//        return new ResponseEntity<>(savedExhibition, HttpStatus.CREATED);
//    }

//    @DeleteMapping("/photo/gallery")
//    public ResponseEntity<String> deletePhotoFromGalleryByKey(@RequestParam("key") String  key, @RequestParam(value = "id") String  id) {
//        storageService.deletePhoto(key);
//        Exhibition exhibition = exhibitionService.getExhibitionById(id);
//        exhibition.getGallery_photos().removeIf(p -> p.getKey().equals(key));
//
//        exhibitionService.updateExhibition(exhibition.getId(), exhibition);
//        return ResponseEntity.ok("Photo deleted successfully!");
//    }

    @GetMapping("{id}")
    public ResponseEntity<Exhibition> getExhibitionById(@PathVariable("id") String id) {

        return ResponseEntity.ok(exhibitionService.getExhibitionById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Exhibition>> getAllExhibitions() {

        return ResponseEntity.ok(exhibitionService.getAllExhibitions());
    }

    @PutMapping(value = "{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<Exhibition> updateExhibition(@PathVariable("id") String  exhibitionId,
                                                       @RequestPart(name = "photos", required = false) List<MultipartFile> photos,
                                                       @RequestPart(name = "image", required = false) MultipartFile image,
                                                       @RequestPart @Valid Exhibition updatedExhibition) throws IOException {

        return ResponseEntity.ok(exhibitionService.updateExhibition(exhibitionId, image, photos, updatedExhibition));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteExhibitionById(@PathVariable("id") String  id) {
        exhibitionService.deleteExhibitionById(id);
        return ResponseEntity.ok("Exhibition deleted successfully!");
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Exhibition>> getExhibitionsByDate(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
                                                                 @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end) {

        return ResponseEntity.ok(exhibitionService.getExhibitionsByDate(start, end));
    }
    @GetMapping("/page")
    public Page<Exhibition> findAllByPage(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int sizePerPage,
                                          @RequestParam(defaultValue = "START_DATE") SortField sortField,
                                          @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        return exhibitionService.findAllByPage(PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName()));
    }

    @GetMapping("/archive")
        public Page<Exhibition> getExhibitionsArchive(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int sizePerPage,
                                                      @RequestParam(defaultValue = "START_DATE") SortField sortField,
                                                      @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        return exhibitionService.getExhibitionsArchive(PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Exhibition>> getExhibitionsByKeySearch(@RequestParam String  key, @RequestParam(defaultValue = "") String oblast) {

        return ResponseEntity.ok(exhibitionService.getExhibitionsByKeySearch(key, oblast));
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
