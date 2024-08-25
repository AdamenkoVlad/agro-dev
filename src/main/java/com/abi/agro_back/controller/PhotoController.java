package com.abi.agro_back.controller;

import com.abi.agro_back.config.StorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/allPhotos")
@Tag(name = "All Photos", description = "All Photos In Digital Ocean")
public class PhotoController {

    @Autowired
    private StorageService storageService;

    @GetMapping()
    public void getAllPhotos() {
        List<String> l = storageService.getImageNames();
        for (String i:l){
            System.out.println(i);
        }
    }

}
