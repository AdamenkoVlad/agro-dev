package com.abi.agro_back.service.impl;

import com.abi.agro_back.collection.ImagePage;
import com.abi.agro_back.collection.Photo;
import com.abi.agro_back.config.StorageService;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.repository.ImagePageRepository;
import com.abi.agro_back.repository.ProductRepository;
import com.abi.agro_back.service.ImagePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
public class ImagePageServiceImpl implements ImagePageService {

    @Autowired
    private ImagePageRepository imagePageRepository;

    @Autowired
    ProductRepository productRepository;
    @Autowired
    private StorageService storageService;

    @Override
    public ImagePage createImagePage(ImagePage imagePage) throws IOException {

        return imagePageRepository.save(imagePage);
    }

    @Override
    public ImagePage getImagePageById(String imagePageId) {

        return imagePageRepository.findById(imagePageId)
                .orElseThrow(() -> new ResourceNotFoundException("ImagePage is not exists with given id : " + imagePageId));
    }

    @Override
    public Page<ImagePage> getAllImagePages(Pageable pageable) {

        return imagePageRepository.findAll(pageable);
    }
    @Override
    public Page<ImagePage> findAllByPage(Pageable pageable) {
        return imagePageRepository.findAllByVisibleIsTrue(pageable);
    }

    @Override
    public ImagePage updateImagePage(String imagePageId, MultipartFile image, List<MultipartFile> photos, ImagePage updatedImagePage) throws IOException {

        ImagePage imagePage = imagePageRepository.findById(imagePageId).orElseThrow(
                () -> new ResourceNotFoundException("ImagePage is not exists with given id: " + imagePageId)
        );

        updatedImagePage.setId(imagePage.getId());

        if (photos != null) {
            for (MultipartFile f : photos) {
                String key = f.getOriginalFilename() + "" + System.currentTimeMillis();
                URL url = storageService.uploadPhoto(f, key);
                Photo photo = new Photo(key, url);
                updatedImagePage.getGalleryPhotos().add(photo);
            }
        }

        if (image != null) {
            String imageKey = System.currentTimeMillis()+ "" + image.getOriginalFilename();
            URL imageUrl = storageService.uploadPhoto(image, imageKey);
            Photo imagePhoto = new Photo(imageKey, imageUrl);
            if (imagePage.getImage() != null) {
                storageService.deletePhoto(imagePage.getImage().getKey());
            }
            updatedImagePage.setImage(imagePhoto);
        }

        return imagePageRepository.save(updatedImagePage);
    }

    @Override
    public void deleteImagePage(String imagePageId) {

       ImagePage imagePage = imagePageRepository.findById(imagePageId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ImagePage is not exists with given id : " + imagePageId));
        if (imagePage.getImage() != null) {
            storageService.deletePhoto(imagePage.getImage().getKey());
        }
        if (imagePage.getGalleryPhotos() != null) {
            for (Photo photo : imagePage.getGalleryPhotos()){
                storageService.deletePhoto(photo.getKey());
            }
        }
        imagePageRepository.deleteById(imagePageId);
        productRepository.deleteAllByImagePageId(imagePageId);
    }

    @Override
    public List<ImagePage> getImagePagesByKeySearch(String key) {
            return imagePageRepository.findImagePagesByKeyWordsIsContainingIgnoreCaseAndVisibleTrue(key);
    }
}
