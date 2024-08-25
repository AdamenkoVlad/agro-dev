package com.abi.agro_back.repository;

import com.abi.agro_back.collection.OblastConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OblastConfigRepository extends MongoRepository<OblastConfig, String> {
    void deleteByOblastAndOldRegion(String oblast, String oldRegion);
    Optional<OblastConfig> findByOblastAndOldRegion(String oblast, String oldRegion);

    List<OblastConfig> findByOblast(String oblast);
}
