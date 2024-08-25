package com.abi.agro_back.service.impl;

import com.abi.agro_back.collection.Banner;
import com.abi.agro_back.collection.Photo;
import com.abi.agro_back.config.StorageService;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.repository.BannerRepository;
import com.abi.agro_back.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepository bannerRepository;
    @Autowired
    private StorageService storageService
            ;

    @Override
    public Banner createBanner(MultipartFile image, Banner banner) throws IOException {

        String imageKey = System.currentTimeMillis() + "" + image.getOriginalFilename();
        URL imageUrl = storageService.uploadPhoto(image, imageKey);
        Photo imagePhoto = new Photo(imageKey, imageUrl);
        banner.setImg(imagePhoto);

        return bannerRepository.save(banner);
    }

    @Override
    public Banner getBannerById(String bannerId) {

        return bannerRepository.findById(bannerId)
                .orElseThrow(() -> new ResourceNotFoundException("Banner is not exists with given id : " + bannerId));
    }

//    @Override
//    public List<Banner> getAllBanners() {
//
//        return bannerRepository.findAll();
//    }

    @Override
    public Banner updateBanner(String bannerId, MultipartFile image, Banner updatedBanner) throws IOException {

        Banner banner = bannerRepository.findById(bannerId).orElseThrow(
                () -> new ResourceNotFoundException("Banner is not exists with given id: " + bannerId)
        );
        updatedBanner.setId(banner.getId());
        if (image != null) {
            String imageKey = System.currentTimeMillis()+ "" + image.getOriginalFilename();
            URL imageUrl = storageService.uploadPhoto(image, imageKey);
            Photo imagePhoto = new Photo(imageKey, imageUrl);
            if (banner.getImg() != null) {
                storageService.deletePhoto(banner.getImg().getKey());
            }
            updatedBanner.setImg(imagePhoto);
        }
        return bannerRepository.save(updatedBanner);
    }

    @Override
    public void deleteBanner(String bannerId) {

        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Banner is not exists with given id : " + bannerId));

        bannerRepository.deleteById(bannerId);
    }

    @Override
    public Page<Banner> findAllByPage(Pageable pageable) {
        return bannerRepository.findAll(pageable);
    }

    @Override
    public List<Banner> getBannersByKeySearch(String key, String oblast) {
        if (!oblast.isEmpty()) {
            return bannerRepository.findBannersByKeyWordsIsContainingIgnoreCaseAndLocationsContainsIgnoreCase(key, oblast);
        } else {
            return bannerRepository.findBannersByKeyWordsIsContainingIgnoreCase(key);
        }
    }

    @Override
    public List<Banner> getAllBannersByOblast(String oblast) {
        return bannerRepository.findBannersByLocationsContainsAndBannerAgroFalse(oblast);
    }
    @Override
    public List<Banner> getAllAgroBannersByOblast(String oblast) {
        return bannerRepository.findBannersByLocationsContainsAndBannerAgroTrue(oblast);
    }
}
