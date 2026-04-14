package com.devfolio.identity.security;

import com.devfolio.identity.domain.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record IdentityPrincipal(UUID id, String email, Role role) {

    // A "record" is a compact immutable class. Java generates:
    //   - constructor(UUID id, String email, Role role)
    //   - id(), email(), role() accessor methods
    //   - equals(), hashCode(), toString()
    // Perfect for a value object that just carries data.
    public Collection<? extends GrantedAuthority> authorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        // Spring Security convention: role authorities start with "ROLE_".
        // So Role.USER becomes "ROLE_USER", Role.ADMIN becomes "ROLE_ADMIN".
        // This is what .hasRole("ADMIN") checks in SecurityConfig.
    }
}
