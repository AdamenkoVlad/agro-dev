package com.abi.agro_back.service;

import com.abi.agro_back.collection.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BannerService {
    Banner createBanner(MultipartFile image, Banner banner) throws IOException;

    Banner getBannerById(String bannerId);

//    List<Banner> getAllBanners();

    Banner updateBanner(String bannerId, MultipartFile image, Banner updatedBanner) throws IOException;

    void deleteBanner(String  bannerId);

    Page<Banner> findAllByPage(Pageable pageable);

    List<Banner> getBannersByKeySearch(String key, String oblast);

    List<Banner> getAllBannersByOblast(String oblast);

    List<Banner> getAllAgroBannersByOblast(String oblast);
}
