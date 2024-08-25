package com.abi.agro_back.service;

import com.abi.agro_back.collection.VillageCouncil;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VillageCouncilService {
    VillageCouncil createVillageCouncil(MultipartFile image, VillageCouncil villageCouncil) throws IOException;

    VillageCouncil getVillageCouncilById(String villageCouncilId);

    List<VillageCouncil> getAllVillageCouncils();

    VillageCouncil updateVillageCouncil(String villageCouncilId, MultipartFile image, VillageCouncil updatedVillageCouncil) throws IOException;

    void deleteVillageCouncil(String  villageCouncilId);

    List<VillageCouncil> getAllVillageCouncilsByRegion(String oblast, String region, String title, String services, String sells, String head);
}
