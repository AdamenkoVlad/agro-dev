package com.abi.agro_back.repository;

import com.abi.agro_back.collection.Banner;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends MongoRepository<Banner, String> {
    List<Banner> findBannersByKeyWordsIsContainingIgnoreCase(String key);
    List<Banner> findBannersByKeyWordsIsContainingIgnoreCaseAndLocationsContainsIgnoreCase(String key, String location);

    List<Banner> findBannersByLocationsContainsAndBannerAgroFalse(String oblast);
    List<Banner> findBannersByLocationsContainsAndBannerAgroTrue(String oblast);
}
