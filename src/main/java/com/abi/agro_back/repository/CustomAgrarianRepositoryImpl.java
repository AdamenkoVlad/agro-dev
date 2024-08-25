package com.abi.agro_back.repository;

import com.abi.agro_back.collection.Agrarian;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class CustomAgrarianRepositoryImpl implements CustomAgrarianRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public long countByCriteria(String oblast, String oldRegion, String title, String services, List<String> sells, String head) {
        Criteria criteria = new Criteria();

        if (oblast != null && !oblast.isEmpty()) {
            criteria.and("oblast").is(oblast);
        }
        if (oldRegion != null && !oldRegion.isEmpty()) {
            criteria.and("oldRegion").is(oldRegion);
        }
        if (title != null && !title.isEmpty()) {
            criteria.and("title").regex(title, "i");
        }
        if (services != null && !services.isEmpty()) {
            criteria.and("services").regex(services, "i");
        }
        if (sells != null && !sells.isEmpty() && !((sells.size() == 1) && sells.get(0).equals(""))) {
            criteria.and("sells").all(sells);
        }
        if (head != null && !head.isEmpty()) {
            criteria.and("head").regex(head, "i");
        }

        Query query = new Query(criteria);
        return mongoTemplate.count(query, Agrarian.class);
    }
    @Override
    public List<Agrarian> findByCriteriaWithAggregation(String oblast, String oldRegion, String title, String services, List<String> sells, String head, int skip, int limit) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (oblast != null && !oblast.isEmpty()) {
            criteriaList.add(Criteria.where("oblast").is(oblast));
        }
        if (oldRegion != null && !oldRegion.isEmpty()) {
            criteriaList.add(Criteria.where("oldRegion").is(oldRegion));
        }
        if (title != null && !title.isEmpty()) {
            criteriaList.add(Criteria.where("title").regex(title, "i"));
        }
        if (services != null && !services.isEmpty()) {
            criteriaList.add(Criteria.where("services").regex(services, "i"));
        }
        if (sells != null && !sells.isEmpty() && !((sells.size() == 1) && sells.get(0).isEmpty())) {
            criteriaList.add(Criteria.where("sells").all(sells));
        }
        if (head != null && !head.isEmpty()) {
            criteriaList.add(Criteria.where("head").regex(head, "i"));
        }

        Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        MatchOperation matchOperation = match(criteria);

        AggregationOperation addFields = context -> new Document("$addFields",
                new Document("fillScore", new Document("$sum", List.of(
                        new Document("$cond", List.of(new Document("$gt", List.of(new Document("$size", "$phones"), 0)), 1, 0)),
                        new Document("$cond", List.of(new Document("$gt", List.of(new Document("$size", "$emails"), 0)), 1, 0)),
                        new Document("$cond", List.of(new Document("$eq", List.of(new Document("$type", "$image"), "object")), 5, 0))
                ))
                ));

        AggregationOperation sortOperation = sort(Sort.Direction.DESC, "fillScore").and(Sort.Direction.ASC, "title");

        Aggregation aggregation = newAggregation(
                matchOperation,
                addFields,
                sortOperation,
                skip(skip),
                limit(limit)
        );

        AggregationResults<Agrarian> results = mongoTemplate.aggregate(aggregation, "agrarians", Agrarian.class);
        return results.getMappedResults();
    }

}
