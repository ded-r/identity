package com.devfolio.identity.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID id) {super("User not found: " + id);}
    // Includes the ID in the message for debugging.
    // Used when GET /users/me looks up a user from the JWT subject
    // but the row was deleted.
}
