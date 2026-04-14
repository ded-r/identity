package com.devfolio.identity.controller;

import com.devfolio.identity.dto.request.LoginRequest;
import com.devfolio.identity.dto.request.RegisterRequest;
import com.devfolio.identity.dto.response.AuthResponse;
import com.devfolio.identity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
            // @Valid triggers Bean Validation on RegisterRequest fields.
            // If validation fails, Spring throws MethodArgumentNotValidException
            // BEFORE this method body runs. GlobalExceptionHandler catches it → 400.
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
        // If credentials are bad, AuthService throws InvalidCredentialsException.
        // GlobalExceptionHandler catches it → 401.
    }
}
