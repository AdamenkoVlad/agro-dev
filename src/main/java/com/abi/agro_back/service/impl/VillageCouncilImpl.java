package com.abi.agro_back.service.impl;

import com.abi.agro_back.collection.Photo;
import com.abi.agro_back.collection.VillageCouncil;
import com.abi.agro_back.config.StorageService;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.repository.VillageCouncilRepository;
import com.abi.agro_back.service.VillageCouncilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Service
public class VillageCouncilImpl implements VillageCouncilService {

    @Autowired
    private VillageCouncilRepository villageCouncilRepository;
    @Autowired
    private StorageService storageService;

    @Override
    public VillageCouncil createVillageCouncil(MultipartFile image, VillageCouncil villageCouncil) throws IOException {

        String imageKey = System.currentTimeMillis() + "" + image.getOriginalFilename();
        URL imageUrl = storageService.uploadPhoto(image, imageKey);
        Photo imagePhoto = new Photo(imageKey, imageUrl);
        villageCouncil.setImage(imagePhoto);

        return villageCouncilRepository.save(villageCouncil);
    }

    @Override
    public VillageCouncil updateVillageCouncil(String villageCouncilId, MultipartFile image, VillageCouncil updatedVillageCouncil) throws IOException {

        VillageCouncil villageCouncil = villageCouncilRepository.findById(villageCouncilId).orElseThrow(
                () -> new ResourceNotFoundException("VillageCouncil is not exists with given id: " + villageCouncilId)
        );
        updatedVillageCouncil.setId(villageCouncil.getId());

        if (image != null) {
            String imageKey = System.currentTimeMillis() + "" + image.getOriginalFilename();
            URL imageUrl = storageService.uploadPhoto(image, imageKey);
            Photo imagePhoto = new Photo(imageKey, imageUrl);
            if (villageCouncil.getImage() != null) {
                storageService.deletePhoto(villageCouncil.getImage().getKey());
            }
            updatedVillageCouncil.setImage(imagePhoto);
        }

        return villageCouncilRepository.save(updatedVillageCouncil);
    }

    @Override
    public VillageCouncil getVillageCouncilById(String villageCouncilId) {

        return villageCouncilRepository.findById(villageCouncilId)
                .orElseThrow(() -> new ResourceNotFoundException("VillageCouncil is not exists with given id : " + villageCouncilId));
    }

    @Override
    public List<VillageCouncil> getAllVillageCouncils() {

        return villageCouncilRepository.findAll();
    }

    @Override
    public void deleteVillageCouncil(String villageCouncilId) {

        VillageCouncil villageCouncil = villageCouncilRepository.findById(villageCouncilId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("VillageCouncil is not exists with given id : " + villageCouncilId));

        villageCouncilRepository.deleteById(villageCouncilId);
    }

    @Override
    public List<VillageCouncil> getAllVillageCouncilsByRegion(String oblast, String region, String title, String services, String sellsStr, String head) {
        String[] splitArray = sellsStr.split(",");

        List<String> sells = Arrays.asList(splitArray);
        return villageCouncilRepository.findByCriteriaWithAggregation(oblast, region, title, services, sells, head);
    }
}
