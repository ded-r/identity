package com.devfolio.identity.service;

import com.devfolio.identity.domain.entity.User;
import com.devfolio.identity.dto.request.LoginRequest;
import com.devfolio.identity.dto.request.RegisterRequest;
import com.devfolio.identity.dto.response.AuthResponse;
import com.devfolio.identity.exception.EmailAlreadyExistsException;
import com.devfolio.identity.repository.UserRepository;
import com.devfolio.identity.config.PasswordEncoderConfig;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoderConfig passwordEncoderConfig, PasswordEncoder passwordEncoder1, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register (RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("", )
        }

        User newUser = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .build();

        userRepository.save(newUser);

        return AuthResponse.builder()
                .email(newUser.getEmail())
                .build();
    }


    public AuthResponse login (LoginRequest loginRequest, User user) {
        if (userRepository.existsByEmail(loginRequest.getEmail()) && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
           return AuthResponse.builder()

                   .build()
        }
    }
}
