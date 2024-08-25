package com.abi.agro_back.auth;

import com.abi.agro_back.collection.Role;
import com.abi.agro_back.collection.User;
import com.abi.agro_back.config.JwtService;
import com.abi.agro_back.config.MailSender;
import com.abi.agro_back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailSender mailSender;

    public AuthenticationResponse register(RegisterRequest request) {
        final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String DIGITS = "0123456789";
        final String SYMBOLS = "!@#$%&*";

        final SecureRandom random = new SecureRandom();
        StringBuilder characterPool = new StringBuilder();
        characterPool.append(LOWERCASE);
        characterPool.append(UPPERCASE);
        characterPool.append(DIGITS);
        characterPool.append(SYMBOLS);
        List<Character> passwordChars = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            char selectedChar = characterPool.charAt(random.nextInt(characterPool.length()));
            passwordChars.add(selectedChar);
        }
        Collections.shuffle(passwordChars);

        StringBuilder password = new StringBuilder(6);
        for (Character c : passwordChars) {
            password.append(c);
        }

        String passForDB = password.toString();
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(passForDB))
                .role(request.getRole())
                .permissions(request.getPermissions())
                .oblasts(new ArrayList<>())
                .endDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3))
                .build();
        repository.save(user);
        mailSender.sendEmail(request.getEmail(), passForDB);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .permissions(user.getPermissions())
                .build();
    }
}
