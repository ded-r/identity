package com.devfolio.identity.dto.response;

import com.devfolio.identity.domain.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String email;
    private Role role;

    private String accessToken;
    private String refreshToken;
}
