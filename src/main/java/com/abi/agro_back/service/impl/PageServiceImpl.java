package com.abi.agro_back.service.impl;

import com.abi.agro_back.collection.Page;
import com.abi.agro_back.collection.PageDto;
import com.abi.agro_back.collection.Photo;
import com.abi.agro_back.config.StorageService;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.repository.PageRepository;
import com.abi.agro_back.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public Page createPage(PageDto page) {
        Page newPage = Page.builder()
                .title(page.getTitle())
                .content(page.getContent())
                .image(page.getImage())
                .createdAt(page.getCreatedAt())
                .build();

        return pageRepository.save(newPage);
    }
    public Page adminApprovesPage(String id) {
        Page page = pageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Page is not exists with given id : " + id));
        page.setPublished(true);
        return pageRepository.save(page);
    }


    @Override
    public Page getPageById(String pageId) {

        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("Page is not exists with given id : " + pageId));

        return page;
    }

    @Override
    public List<Page> getAllPages() {

        return pageRepository.findAll();
    }

    @Override
    public List<Page> getAllForApprovePages() {
        return pageRepository.findAllByPublishedFalse();
    }

    @Override
    public org.springframework.data.domain.Page<Page> findAllByPage(Pageable pageable) {
        return pageRepository.findAllByPublishedTrue(pageable);
    }
    @Override
    public Page updatePage(String pageId, MultipartFile image, Page updatedPage) throws IOException {

        Page page = pageRepository.findById(pageId).orElseThrow(
                () -> new ResourceNotFoundException("Page is not exists with given id: " + pageId)
        );
        updatedPage.setId(page.getId());

        if (image != null) {
            String imageKey = System.currentTimeMillis()+ "" + image.getOriginalFilename();
            URL imageUrl = storageService.uploadPhoto(image, imageKey);
            Photo imagePhoto = new Photo(imageKey, imageUrl);
            if (page.getImage() != null) {
                storageService.deletePhoto(page.getImage().getKey());
            }
            updatedPage.setImage(imagePhoto);
        }

        return pageRepository.save(updatedPage);
    }

    @Override
    public void deletePage(String pageId) {

        Page page = pageRepository.findById(pageId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Page is not exists with given id : " + pageId));

        pageRepository.deleteById(pageId);
    }
}
