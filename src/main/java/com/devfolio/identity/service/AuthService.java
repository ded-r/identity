package com.devfolio.identity.service;

import com.devfolio.identity.domain.entity.User;
import com.devfolio.identity.domain.enums.Role;
import com.devfolio.identity.dto.request.LoginRequest;
import com.devfolio.identity.dto.request.RegisterRequest;
import com.devfolio.identity.dto.response.AuthResponse;
import com.devfolio.identity.exception.EmailAlreadyExistsException;
import com.devfolio.identity.exception.InvalidCredentialsException;
import com.devfolio.identity.exception.UsernameAlreadyExistsException;
import com.devfolio.identity.repository.UserRepository;
import com.devfolio.identity.security.jwt.JwtService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    // For database operations (find, save, exists checks).

    private final PasswordEncoder passwordEncoder;
    // The BCryptPasswordEncoder BEAN from PasswordEncoderConfig.
    // NOT PasswordEncoderConfig itself — inject the product, not the factory.

    private final JwtService jwtService;
    // To create access + refresh tokens after register/login.

    @Transactional
    // Wraps the method in a DB transaction: if anything throws, the save is rolled back.
    public AuthResponse register (RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
            // Not RuntimeException — specific type that GlobalExceptionHandler maps to 409.
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                // passwordEncoder.encode: takes plain "myPassword123" and returns
                // a BCrypt hash like "$2a$10$abc...xyz". This is what gets saved in DB.
                // The plain password is NEVER stored.
                .fullName(request.getFullName())
                .role(Role.USER)
                // @Builder.Default would handle this, but being explicit is clearer.
                .build();

        userRepository.save(newUser);
        // INSERT INTO users (...) VALUES (...).
        // @PrePersist sets createdAt and updatedAt before the INSERT.
        // @GeneratedValue(UUID) sets the id.

        return AuthResponse.builder()
                .email(newUser.getEmail())
                .role(newUser.getRole())
                .accessToken(jwtService.createAccessToken(newUser))
                .refreshToken(jwtService.createRefreshToken(newUser))
                .build();
        // Return tokens immediately after registration so the user
        // doesn't need a separate login call.

    }

    @Transactional(readOnly = true)
    // readOnly = true: optimization hint. No writes expected.
    public AuthResponse login (LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);
        // If email doesn't exist → throw "Invalid email or password."
        // Same message whether email is wrong or password is wrong

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        // passwordEncoder.matches(rawPassword, storedHash):
        //   - First argument: what the user just typed
        //   - Second argument: the BCrypt hash from the database
        // BCrypt extracts the salt from the stored hash and re-hashes the raw password
        // to compare. Returns true if they match.
        // BUG FIX: your commented code had matches(password, password) — comparing to itself.


        return AuthResponse.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(jwtService.createAccessToken(user))
                .refreshToken(jwtService.createRefreshToken(user))
                .build();
    }
}
