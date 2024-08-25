package com.abi.agro_back.repository;

import com.abi.agro_back.collection.Role;
import com.abi.agro_back.collection.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Page<User> findByOrderByIdDesc(Pageable pageable);
    Page<User> findAllByRoleOrderByIdDesc(Role role, Pageable pageable);
}
