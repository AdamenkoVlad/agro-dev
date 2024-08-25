package com.abi.agro_back.repository;

import com.abi.agro_back.collection.Agrarian;

import java.util.List;

public interface CustomAgrarianRepository {
    long countByCriteria(String oblast, String oldRegion, String title, String services, List<String> sells, String head);
    List<Agrarian> findByCriteriaWithAggregation(String oblast, String oldRegion, String title, String services, List<String> sells, String head, int skip, int limit);

}