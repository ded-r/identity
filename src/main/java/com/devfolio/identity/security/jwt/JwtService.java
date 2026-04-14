package com.devfolio.identity.security.jwt;

import com.devfolio.identity.config.JwtConfig;
import com.devfolio.identity.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    public String createAccessToken(User user) {
        long lifetimeSeconds = jwtConfig.getAccessTokenMinutes() * 60L;
        return buildToken(user, "access", lifetimeSeconds);
        // "access" is a custom claim we put in the token so we can distinguish
        // access tokens from refresh tokens when validating.
    }

    public String createRefreshToken(User user) {
        long lifetimeSeconds = jwtConfig.getRefreshTokenDays() * 24L * 60L * 60L;
        return buildToken(user, "refresh", lifetimeSeconds);
    }

    public Claims parseAccessToken(String token) {
        // Parses and validates the token:
        // 1. Checks the signature (was it signed with OUR secret key?)
        // 2. Checks expiry (is "exp" in the future?)
        // 3. Returns the claims if valid; throws an exception if not.
        Claims claims = parseToken(token);

        // Extra check: make sure this is an ACCESS token, not a refresh token.
        // Without this, someone could use a refresh token as an access token.
        String type = claims.get("type", String.class);
        if (!"access".equals(type)) {
            throw new IllegalArgumentException("Invalid token type: " + type);
        }

        return claims;
    }

    private String buildToken(User user, String tokenType, long lifetimeSeconds) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(lifetimeSeconds);

        return Jwts.builder()
                .subject(user.getId().toString())
                // "sub" claim = who this token is about. We use the user's UUID.
                // UUID is stable (email can change), unique, and not sensitive.

                .claim("email", user.getEmail())
                // Custom claim. Lets us show the email in responses without
                // a DB lookup when we only need display info.

                .claim("role", user.getRole().name())
                // Custom claim. "USER" or "ADMIN". The JWT filter reads this
                // to build Spring Security authorities.

                .claim("type", tokenType)
                // "access" or "refresh". Prevents using the wrong token type.

                .issuedAt(Date.from(now))
                // "iat" claim = when the token was created.

                .expiration(Date.from(expiry))
                // "exp" claim = when the token dies. jjwt automatically rejects
                // expired tokens during parsing.

                .signWith(getSigningKey())
                // Sign with our secret key using HS256 (HMAC-SHA256).
                // This is what makes the token tamper-proof: if anyone changes
                // the payload, the signature won't match.

                .compact();
        // Serialize to the final "header.payload.signature" string.
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                // Use the same key that signed the token to verify it.
                .build()
                .parseSignedClaims(token)
                // Verifies signature + checks expiry. Throws:
                //   - ExpiredJwtException if the token is past its "exp"
                //   - SignatureException if the signature doesn't match
                //   - MalformedJwtException if the string isn't a valid JWT
                .getPayload();
        // Returns the Claims (the payload part of the JWT).
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
        // Converts the secret string to a SecretKey object.
        // HS256 needs at least 256 bits (32 bytes). If your string is shorter,
        // jjwt throws an error telling you the key is too weak.
    }
}
