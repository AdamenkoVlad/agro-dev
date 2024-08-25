package com.abi.agro_back.repository;

import com.abi.agro_back.collection.RegisteringRequest;
import com.abi.agro_back.collection.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RegisteringRequestRepository extends MongoRepository<RegisteringRequest, String> {

}
