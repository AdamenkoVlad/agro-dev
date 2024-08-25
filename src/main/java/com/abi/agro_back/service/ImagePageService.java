package com.abi.agro_back.service;

import com.abi.agro_back.collection.ImagePage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImagePageService {
    ImagePage createImagePage(ImagePage imagePage) throws IOException;

    ImagePage getImagePageById(String imagePageId);

    Page<ImagePage> getAllImagePages(Pageable pageable);

    ImagePage updateImagePage(String imagePageId, MultipartFile image, List<MultipartFile> photos, ImagePage updatedImagePage) throws IOException;

    void deleteImagePage(String  imagePageId);

    Page<ImagePage> findAllByPage(Pageable pageable);

    List<ImagePage> getImagePagesByKeySearch(String key);
}
