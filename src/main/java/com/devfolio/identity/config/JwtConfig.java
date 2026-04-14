package com.devfolio.identity.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {

    private String secret;
    private int accessTokenMinutes;
    private int refreshTokenDays;
}
