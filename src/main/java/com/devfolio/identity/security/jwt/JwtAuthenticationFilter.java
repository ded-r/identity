package com.devfolio.identity.security.jwt;

import com.devfolio.identity.domain.enums.Role;
import com.devfolio.identity.security.IdentityPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter: guarantees this filter runs exactly once per request,
    // even if the request is forwarded internally.

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // ── Step 1: Extract the token from the Authorization header ──

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        // HttpHeaders.AUTHORIZATION = "Authorization"

        if (header == null || !header.startsWith("Bearer ")) {
            // No token present. Let the request continue WITHOUT authentication.
            // Public endpoints (register, login) will work fine.
            // Protected endpoints will be rejected by Spring Security's
            // "anyRequest().authenticated()" rule later in the filter chain.
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        // Skip "Bearer " (7 characters) to get the raw JWT string.

        // ── Step 2: Validate the token and extract claims ──

        Claims claims;
        try {
            claims = jwtService.parseAccessToken(token);
            // If this succeeds, the token is:
            //   - signed with our key (not tampered)
            //   - not expired
            //   - an "access" type token (not refresh)
        } catch (Exception e) {
            // Token is invalid, expired, or malformed.
            // Return 401 immediately. Do NOT continue the filter chain.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // ── Step 3: Build the principal and set the security context ──

        UUID userId = UUID.fromString(claims.getSubject());
        String email = claims.get("email", String.class);
        Role role = Role.valueOf(claims.get("role", String.class));

        IdentityPrincipal principal = new IdentityPrincipal(userId, email, role);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,          // the principal (who)
                null,               // credentials (null — we already validated the token)
                principal.authorities()  // granted authorities (ROLE_USER, etc.)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // From this point on, Spring Security considers this request "authenticated."
        // Controllers can use @AuthenticationPrincipal IdentityPrincipal to get "principal".

        // ── Step 4: Continue the filter chain ──
        filterChain.doFilter(request, response);
    }
}

// Is there a Bearer xxx header? No → skip, let Spring handle it (public or 401).
// Yes → parse the token. Invalid → 401 immediately.
// Valid → build an IdentityPrincipal, wrap in Authentication, put in SecurityContextHolder.
// Continue. The controller now sees an authenticated user.