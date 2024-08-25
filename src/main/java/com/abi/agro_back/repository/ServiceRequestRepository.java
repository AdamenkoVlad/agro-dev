package com.abi.agro_back.repository;

import com.abi.agro_back.collection.ServiceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ServiceRequestRepository extends MongoRepository<ServiceRequest, String> {

}
