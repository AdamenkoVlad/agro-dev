package com.abi.agro_back.repository;

import com.abi.agro_back.collection.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PasswordTokenRepository extends MongoRepository<PasswordResetToken, String> {
   PasswordResetToken findByToken(String email);
}
