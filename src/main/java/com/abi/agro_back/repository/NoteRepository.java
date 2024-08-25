package com.abi.agro_back.repository;

import com.abi.agro_back.collection.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findAllByAgrarianIdIsAndUserIdIsOrderByCreatedAtDesc(String agrarianId, String userId);
}
