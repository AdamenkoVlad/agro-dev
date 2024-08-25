package com.abi.agro_back.service;

import com.abi.agro_back.collection.User;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User createUser(User user);

    User getUserById(String userId);

    org.springframework.data.domain.Page<User> getAllUsers(Pageable pageable);

    User updateUser(String userId, User updatedUser);

    void deleteUser(String  userId);

    User findUserByEmail(String userEmail);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    User getUserByPasswordResetToken(String token);

    void changeUserPassword(User user, String password);

    Boolean checkOblastApprove(String oblast);
}
