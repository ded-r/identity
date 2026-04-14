package com.devfolio.identity.service;

import com.devfolio.identity.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    // Contract: "anyone who needs user-read operations depends on this interface."

    UserResponse getUserById(UUID id);
    // Loads a user by primary key and returns a SAFE view (no password).
    // Used by: UserController.me() — reads the user ID from the JWT principal.
}
