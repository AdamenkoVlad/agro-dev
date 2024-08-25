package com.abi.agro_back.service.impl;

import com.abi.agro_back.collection.PasswordResetToken;
import com.abi.agro_back.collection.Role;
import com.abi.agro_back.collection.User;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.repository.PasswordTokenRepository;
import com.abi.agro_back.repository.UserRepository;
import com.abi.agro_back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {

        return userRepository.save(user);
    }

    @Override
    public User getUserById(String userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not exists with given id : " + userId));
    }

    @Override
    public org.springframework.data.domain.Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findByOrderByIdDesc( pageable);
    }

    @Override
    public User updateUser(String userId, User updatedUser) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User is not exists with given id: " + userId)
        );

        updatedUser.setId(userId);

        return userRepository.save(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {

       User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User is not exists with given id : " + userId));

       userRepository.deleteById(userId);
    }
 
    @Override
    public User findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User is not exists with given email : " + userEmail));
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
        passwordTokenRepository.save(myToken);

    }

    @Override
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

    }

    @Override
    public Boolean checkOblastApprove(String oblast) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            User user = (User) authentication.getPrincipal();
            return user.getOblasts().contains(oblast) && user.getEndDate().after(new Date(System.currentTimeMillis()));
        }
        throw new ResourceNotFoundException("User not authenticated");
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    @Override
    public User getUserByPasswordResetToken(String token) {
        return passwordTokenRepository.findByToken(token).getUser();


    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -10);
        return passToken.getExpiryDate().before(cal.getTime());
    }
}
