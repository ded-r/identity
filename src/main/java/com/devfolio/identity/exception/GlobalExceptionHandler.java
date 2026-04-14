package com.devfolio.identity.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
// Combines @ControllerAdvice + @ResponseBody.
// Methods here catch exceptions thrown by ANY controller
// and convert them into consistent JSON responses.

public class GlobalExceptionHandler {

    // ── 400 Bad Request: validation errors ──
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        // Joins all field errors: "email: Email is required; password: Password is required"

        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // ── 409 Conflict: duplicate email or username ──
    @ExceptionHandler({EmailAlreadyExistsException.class, UsernameAlreadyExistsException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(
            RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // ── 401 Unauthorized: bad login credentials ──
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            InvalidCredentialsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    // ── 404 Not Found: user not found ──
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            InvalidCredentialsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // ── Helper: build a consistent JSON error body ──
    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String message, HttpServletRequest request) {

        Map<String, Object> body = Map.of(
                "status", status.value(),           // e.g. 409
                "error", status.getReasonPhrase(),   // e.g. "Conflict"
                "message", message,                  // e.g. "Email is already registered"
                "path", request.getRequestURI(),     // e.g. "/auth/register"
                "timestamp", Instant.now().toString() // e.g. "2026-04-09T..."
        );

        return ResponseEntity.status(status).body(body);
        // Sets the HTTP status code AND returns the JSON body.
    }
}
