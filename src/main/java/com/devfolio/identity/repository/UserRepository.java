package com.devfolio.identity.repository;

import com.devfolio.identity.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
