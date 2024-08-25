package com.abi.agro_back.repository;

import com.abi.agro_back.collection.VillageCouncil;

import java.util.List;

public interface CustomVillageCouncilRepository {
    List<VillageCouncil> findByCriteriaWithAggregation(String oblast, String oldRegion, String title, String services, List<String> sells, String head);

}