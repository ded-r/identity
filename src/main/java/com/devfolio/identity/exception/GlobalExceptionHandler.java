package com.devfolio.identity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler { // Global exception handler for REST

    @ResponseStatus(HttpStatus.UNAUTHORIZED) // Maps to 401 Unauthorized
    @ExceptionHandler(InvalidCredentialsException.class)
    public String handleUnauthorized(InvalidCredentialsException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND) // Maps to 404 Not Found
    @ExceptionHandler(UserNotFoundException.class)
    public String handleNotFound(UserNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT) // Maps to 409 Conflict
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleAlreadyExists(EmailAlreadyExistsException ex) {
        return ex.getMessage();
    }

}
