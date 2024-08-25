package com.abi.agro_back.service.impl;

import com.abi.agro_back.collection.Agrarian;
import com.abi.agro_back.collection.VillageCouncil;
import com.abi.agro_back.repository.AgrarianRepository;
import com.abi.agro_back.repository.DemoRepository;
import com.abi.agro_back.repository.VillageCouncilRepository;
import com.abi.agro_back.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    private AgrarianRepository agrarianRepository;
    @Autowired
    private DemoRepository demoRepository;
    @Autowired
    private VillageCouncilRepository villageCouncilRepository;

    @Override
    public Page<Agrarian> getAllAgrariansByRegion(Pageable pageable, String title, String services, String sellsStr, String head) {
        String oblast = demoRepository.findAll().get(0).getOblast();
        String region = demoRepository.findAll().get(0).getOldRegion();
        String[] splitArray = sellsStr.split(",");

        List<String> sells = Arrays.asList(splitArray);
        int skip = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        List<Agrarian> documents = agrarianRepository.findByCriteriaWithAggregation(oblast, region, title, services, sells, head, skip, limit);
        long totalCount = agrarianRepository.countByCriteria(oblast, region, title, services, sells, head);

        return new PageImpl<>(documents, pageable, totalCount);
    }

    @Override
    public List<Agrarian> getAllDemoAgrarians() {
        String oblast = demoRepository.findAll().get(0).getOblast();
        String region = demoRepository.findAll().get(0).getOldRegion();
        return agrarianRepository.findAllByOblastAndOldRegion(oblast, region);
    }

    @Override
    public List<Agrarian> getAllExcelAgrarians(String obl, String reg) {
        return agrarianRepository.findAllByOblastAndOldRegion(obl, reg);
    }

    @Override
    public List<VillageCouncil> getAllExcelVillageCouncils(String obl, String reg) {
        return villageCouncilRepository.findAllByOblastAndOldRegion(obl, reg);
    }

    @Override
    public List<Agrarian> getAllExcelOblAgrarians(String obl) {
        return agrarianRepository.findAllByOblast(obl);
    }

    @Override
    public List<VillageCouncil> getAllExcelOblVillageCouncils(String obl) {
        return villageCouncilRepository.findAllByOblast(obl);
    }

    @Override
    public List<VillageCouncil> getAllVillageCouncilsByRegion(String title, String services, String sellsStr, String head) {
        String oblast = demoRepository.findAll().get(0).getOblast();
        String region = demoRepository.findAll().get(0).getOldRegion();
        String[] splitArray = sellsStr.split(",");

        List<String> sells = Arrays.asList(splitArray);

        return villageCouncilRepository.findByCriteriaWithAggregation(oblast, region, title, services, sells, head);
    }

    @Override
    public List<VillageCouncil> getAllDemoVillageCouncils() {
        String oblast = demoRepository.findAll().get(0).getOblast();
        String region = demoRepository.findAll().get(0).getOldRegion();

        return villageCouncilRepository.findAllByOblastAndOldRegion(oblast, region);
    }
}
