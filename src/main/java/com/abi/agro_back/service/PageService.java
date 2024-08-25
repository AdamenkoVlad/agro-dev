package com.abi.agro_back.service;

import com.abi.agro_back.collection.Page;
import com.abi.agro_back.collection.PageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PageService {
    Page createPage(PageDto page);

    Page adminApprovesPage(String id);

    Page getPageById(String pageId);

    List<Page> getAllPages();

    List<Page> getAllForApprovePages();

    org.springframework.data.domain.Page<Page> findAllByPage(Pageable pageable);

    Page updatePage(String pageId, MultipartFile image, Page updatedPage) throws IOException;

    void deletePage(String  pageId);

}
