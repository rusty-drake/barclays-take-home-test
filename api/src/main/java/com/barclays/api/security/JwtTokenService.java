package com.barclays.api.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenService {

    private final SecretKey secretKey;
    private final String expectedIssuer;

    public JwtTokenService(
            @Value("${auth.jwt.secret}") String secret,
            @Value("${auth.jwt.issuer}") String issuer
    ) {
        // secret must be long enough for HS256 (at least 32 bytes)
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expectedIssuer = issuer;
    }

    public String getEmailFromToken(String token) throws JwtException {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .requireIssuer(expectedIssuer)
                .build()
                .parseClaimsJws(token);

        Claims claims = jws.getBody();

        return claims.get("email", String.class);
    }
}
