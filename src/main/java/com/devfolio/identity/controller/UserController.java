package com.devfolio.identity.controller;

import com.devfolio.identity.dto.response.UserResponse;
import com.devfolio.identity.security.IdentityPrincipal;
import com.devfolio.identity.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal IdentityPrincipal principal) {
        // @AuthenticationPrincipal: Spring extracts the principal object
        // that JwtAuthenticationFilter put into the SecurityContext.
        // It is our IdentityPrincipal record with id(), email(), role().

        return userService.getUserById(principal.id());
        // principal.id() is the UUID from the JWT "sub" claim.
        // We reload from DB to get the freshest data (e.g., if role changed
        // after the token was issued).
    }
}

// Why this endpoint doesn't need @PreAuthorize:
// SecurityConfig already says anyRequest().authenticated(),
// so /users/me is automatically blocked without a valid token.
// You only need @PreAuthorize for role-based restrictions beyond "must be logged in."
