package com.devfolio.identity.service.impl;

import com.devfolio.identity.domain.entity.User;
import com.devfolio.identity.dto.response.UserResponse;
import com.devfolio.identity.exception.UserNotFoundException;
import com.devfolio.identity.repository.UserRepository;
import com.devfolio.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    // "implements UserService" means this class IS the real UserService bean.
    // When controllers ask for UserService, Spring gives them this.

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        // findById is free from JpaRepository. Returns Optional<User>.
        // If the user was deleted after the JWT was issued, this throws → 404.

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
        // Note: password is NOT included. UserResponse has no password field.
        // This is the "librarian" pattern: convert entity → safe DTO.
    }
}
