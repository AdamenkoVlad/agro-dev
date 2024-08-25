package com.abi.agro_back.repository;

import com.abi.agro_back.collection.Exhibition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExhibitionRepository extends MongoRepository<Exhibition, String> {
    List<Exhibition> findExhibitionsByStartDateIsBetweenOrEndDateIsBetweenOrStartDateBeforeAndEndDateAfterOrderByStartDate(Date from, Date to, Date from1, Date to1, Date from2, Date to2);

    List<Exhibition> findAllByStartDateBeforeAndEndDateAfter(Date from, Date to);
    Page<Exhibition> findExhibitionsByEndDateBeforeOrderByEndDateDesc(Date data, Pageable pageable);
    Page<Exhibition> findExhibitionsByEndDateAfterOrEndDateIsNullOrderByEndDateDesc(Date now, Pageable pageable);

    List<Exhibition> findExhibitionsByKeyWordsIsContainingIgnoreCase(String key);
    List<Exhibition> findExhibitionsByKeyWordsIsContainingIgnoreCaseAndLocationIsIgnoreCase(String key, String oblast);

}
