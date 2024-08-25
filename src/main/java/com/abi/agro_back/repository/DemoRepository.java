package com.abi.agro_back.repository;

import com.abi.agro_back.collection.DemoConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoRepository extends MongoRepository<DemoConfig, String> {
}
