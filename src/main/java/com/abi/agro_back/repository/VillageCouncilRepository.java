package com.abi.agro_back.repository;

import com.abi.agro_back.collection.VillageCouncil;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillageCouncilRepository extends MongoRepository<VillageCouncil, String>, CustomVillageCouncilRepository {
    @Aggregation(pipeline = {
            "{'$match': {'oblast': ?0, 'oldRegion': ?1}}",
            "{ $addFields: { 'fillScore': { $sum: [ { $cond: [{ $gt: [{ $size: '$phones' }, 0] }, 1, 0] }, { $cond: [{ $gt: [{ $size: '$emails' }, 0] }, 1, 0] }, {$cond: [{ $eq: [{ $type: '$image' }, 'object'] }, 1, 0]} ] } } }",
            "{ $sort: { 'fillScore': -1, 'title': 1 } }"
    })
    List<VillageCouncil> findAllByOblastAndOldRegionOrderByImageDescTitleAsc(String oblast, String oldRegion);
    long countAllByOblastEquals(String oblast);
    long countAllByOblastEqualsAndOldRegionEquals(String oblast, String region);
    @Aggregation(pipeline = {
            "{'$match': {'oblast': ?0, 'oldRegion': ?1}}",
            "{ $addFields: { 'fillScore': { $sum: [ { $cond: [{ $gt: [{ $size: '$phones' }, 0] }, 1, 0] }, { $cond: [{ $gt: [{ $size: '$emails' }, 0] }, 1, 0] }] } } }",
            "{ $sort: { 'fillScore': -1, 'title': 1 } }"
    })
    List<VillageCouncil> findAllByOblastAndOldRegion(String oblast, String region);

    @Aggregation(pipeline = {
            "{'$match': {'oblast': ?0}}",
            "{ $addFields: { 'fillScore': { $sum: [ { $cond: [{ $gt: [{ $size: '$phones' }, 0] }, 1, 0] }, { $cond: [{ $gt: [{ $size: '$emails' }, 0] }, 1, 0] }] } } }",
            "{ $sort: { 'fillScore': -1, 'title': 1 } }"
    })
    List<VillageCouncil> findAllByOblast(String oblast);

}
