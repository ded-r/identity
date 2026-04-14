package com.devfolio.identity.repository;

import com.devfolio.identity.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
// Spring Data reads method names and generates SQL automatically.

    Optional<User> findByEmail(String email);
    // SELECT * FROM users WHERE email = ?
    // Returns Optional because the email might not exist.
    // Used by: login (find user to check password)

    Optional<User> findByUsername(String email);
    // SELECT * FROM users WHERE username = ?
    // Used by: UserService if you look up by username

    boolean existsByEmail(String email);
    // SELECT COUNT(*) > 0 FROM users WHERE email = ?
    // Returns true/false. Used by: register (check for duplicate email)

    boolean existsByUsername(String username);
    // Same pattern for username uniqueness check.

}
