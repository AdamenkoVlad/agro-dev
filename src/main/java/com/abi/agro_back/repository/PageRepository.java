package com.abi.agro_back.repository;

import com.abi.agro_back.collection.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends MongoRepository<Page, String> {
    org.springframework.data.domain.Page<Page> findAllByPublishedTrue(Pageable pageable);

    List<Page> findAllByPublishedFalse();
}
