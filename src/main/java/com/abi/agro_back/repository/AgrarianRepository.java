package com.abi.agro_back.repository;

import com.abi.agro_back.collection.Agrarian;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgrarianRepository extends MongoRepository<Agrarian, String>, CustomAgrarianRepository {
    List<Agrarian> findAllByOrderByIsPriorityDescTitleAsc();
    @Aggregation(pipeline = {
                "{ '$match': {"
                        + "'oblast': ?0,"
                        + "'oldRegion': ?1,"
                        + "$or: ["
                        + "{ 'title': { '$regex': ?2, '$options': 'i' } },"
                        + "{ 'title': '' }"
                        + "],"
                        + "$or: ["
                        + "{ 'services': { '$regex': ?3, '$options': 'i' } },"
                        + "{ 'services': '' }"
                        + "],"
                        + "$or: ["
                        + "{ 'sells': { '$regex': ?4, '$options': 'i' } },"
                        + "{ 'sells': '' }"
                        + "],"
                        + "$or: ["
                        + "{ 'head': { '$regex': ?5, '$options': 'i' } },"
                        + "{ 'head': '' }"
                        + "]"
                        + "} }",
                "{ $addFields: { 'fillScore': { $sum: ["
                        + "{ $cond: [ { $gt: [ { $size: '$phones' }, 0 ] }, 1, 0 ] },"
                        + "{ $cond: [ { $gt: [ { $size: '$emails' }, 0 ] }, 1, 0 ] },"
                        + "{ $cond: [ { $eq: [ { $type: '$image' }, 'object' ] }, 5, 0 ] }"
                        + "] } } }",
                "{ $sort: { 'fillScore': -1, 'title': 1 } }",
                "{ '$skip': ?6 }",
                "{ '$limit': ?7 }"
        })
//    @Aggregation(pipeline = {
//            "{'$match': {'$and': [{'oblast': ?0, 'oldRegion': ?1}, { '$and': [ { 'title': { '$regex': ?2, '$options': 'i' } }, { 'services': { '$regex': ?3, '$options': 'i' } }, { 'sells': { '$regex': ?4, '$options': 'i' } }, { 'head': { '$regex': ?5, '$options': 'i' } } ]}]}}",
//            "{ $addFields: { 'fillScore': { $sum: [ { $cond: [{ $gt: [{ $size: '$phones' }, 0] }, 1, 0] }, { $cond: [{ $gt: [{ $size: '$emails' }, 0] }, 1, 0] }, {$cond: [{ $eq: [{ $type: '$image' }, 'object'] }, 5, 0]} ] } } }",
//            "{ $sort: { 'fillScore': -1, 'title': 1 } }",
//            "{'$skip': ?6}",
//            "{'$limit': ?7}"
//    })
//    @Aggregation(pipeline = {
//            "{ '$match': { '$and': ["
//                    + "{ 'oblast': ?0 },"
//                    + "{ 'oldRegion': ?1 },"
//                    + "{ $or: [ { 'title': { '$regex': ?2, '$options': 'i' } }, { ?2: '' } ] },"
//                    + "{ $or: [ { 'services': { '$regex': ?3, '$options': 'i' } }, { ?3: '' } ] },"
//                    + "{ $or: [ { 'sells': { '$regex': ?4, '$options': 'i' } }, { ?4: '' } ] },"
//                    + "{ $or: [ { 'head': { '$regex': ?5, '$options': 'i' } }, { ?5: '' } ] }"
//                    + "] } }",
//            "{ $addFields: { 'fillScore': { $sum: ["
//                    + "{ $cond: [ { $gt: [ { $size: '$phones' }, 0 ] }, 1, 0 ] },"
//                    + "{ $cond: [ { $gt: [ { $size: '$emails' }, 0 ] }, 1, 0 ] },"
//                    + "{ $cond: [ { $eq: [ { $type: '$image' }, 'object' ] }, 5, 0 ] }"
//                    + "] } } }",
//            "{ $sort: { 'fillScore': -1, 'title': 1 } }",
//            "{ '$skip': ?6 }",
//            "{ '$limit': ?7 }"
//    })
    List<Agrarian> findAllByOblastAndOldRegion(String oblast, String region, String title, String services, String sells, String head, int skip, int limit);
    @Aggregation(pipeline = {
            "{'$match': {'oblast': ?0, 'oldRegion': ?1}}",
            "{ $addFields: { 'fillScore': { $sum: [ { $cond: [{ $gt: [{ $size: '$phones' }, 0] }, 1, 0] }, { $cond: [{ $gt: [{ $size: '$emails' }, 0] }, 1, 0] }, {$cond: [{ $eq: [{ $type: '$image' }, 'object'] }, 5, 0]} ] } } }",
            "{ $sort: { 'fillScore': -1, 'title': 1 } }",
            "{'$skip': ?2}",
            "{'$limit': ?3}"
    })
    List<Agrarian> findAllByOblastAndOldRegionDemo(String oblast, String region, int skip, int limit);
    List<Agrarian> findAllByOblastAndOldRegionOrderByImageDescTitleAsc(String oblast, String region);
    long countAllByOblastEquals(String oblast);
    long countAllByOblastEqualsAndOldRegionEquals(String oblast, String region);
    @Aggregation(pipeline = {
            "{'$match': {'oblast': ?0, 'oldRegion': ?1}}",
            "{ $addFields: { 'fillScore': { $sum: [ { $cond: [{ $gt: [{ $size: '$phones' }, 0] }, 1, 0] }, { $cond: [{ $gt: [{ $size: '$emails' }, 0] }, 1, 0] }] } } }",
            "{ $sort: { 'fillScore': -1, 'title': 1 } }"
    })
    List<Agrarian> findAllByOblastAndOldRegion(String oblast, String region);

    @Aggregation(pipeline = {
            "{'$match': {'oblast': ?0}}",
            "{ $addFields: { 'fillScore': { $sum: [ { $cond: [{ $gt: [{ $size: '$phones' }, 0] }, 1, 0] }, { $cond: [{ $gt: [{ $size: '$emails' }, 0] }, 1, 0] }] } } }",
            "{ $sort: { 'fillScore': -1, 'title': 1 } }"
    })
    List<Agrarian> findAllByOblast(String oblast);
}
