package com.abi.agro_back.repository;

import com.abi.agro_back.collection.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findProductsByImagePageId(String id);

    void deleteAllByImagePageId(String id);
}
