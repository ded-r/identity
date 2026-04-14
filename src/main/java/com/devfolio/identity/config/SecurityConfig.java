package com.devfolio.identity.config;

import com.devfolio.identity.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
// Enables @PreAuthorize("hasRole('ADMIN')") on individual controller methods.
// Without this, @PreAuthorize annotations are silently ignored.

@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                // Disable CSRF protection. Why?
                // CSRF protects against attacks where a browser auto-sends cookies.
                // Our API uses Bearer tokens in headers, not cookies.
                // No cookie = no CSRF risk for this pattern.

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // STATELESS = Spring Security will NEVER create an HTTP session.
                // Every request must carry its own proof (the JWT).
                // This is the core of "stateless auth."

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        // These two endpoints work WITHOUT any token.
                        // /auth/register creates users. /auth/login issues tokens.
                        // You can't require a token to GET a token.

                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        // Only users with ROLE_ADMIN authority can access health check paths.

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Only users with ROLE_ADMIN authority can access /admin/ paths.
                        // (The JWT filter sets authorities from the "role" claim.)

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                // Insert our JWT filter BEFORE Spring's built-in username/password filter.
                // This means our filter runs first: it reads the Bearer token and sets
                // the authentication. Spring's default filter then sees "already authenticated"
                // and does nothing.

        // NOTE: No .formLogin() and no .httpBasic()
        // We removed them. This is a JSON API — no HTML login page, no basic auth.

        return httpSecurity.build();
    }
}
