package com.barclays.auth.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
public class JwtServiceUnitTest {

    private JwtService sut;
    private final String testSecret = "myTestSecretKeyThatIsLongEnoughForHS256Algorithm";
    private final String testIssuer = "test-issuer";
    private final long testExpirySeconds = 3600L; // 1 hour

    @BeforeEach
    public void setup() {
        sut = new JwtService(testSecret, testIssuer, testExpirySeconds);
    }

    @Test
    public void generateTokenReturnsValidJwtToken() {
        // given
        String subject = "test@example.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", subject);
        claims.put("role", "USER");

        // when
        String token = sut.generateToken(subject, claims);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        
        // Verify token structure (JWT tokens have 3 parts separated by dots)
        String[] tokenParts = token.split("\\.");
        assertThat(tokenParts).hasSize(3);
    }

    @Test
    public void generateTokenContainsCorrectSubject() {
        // given
        String subject = "user@barclays.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", subject);

        // when
        String token = sut.generateToken(subject, claims);

        // then
        Claims parsedClaims = parseToken(token);
        assertThat(parsedClaims.getSubject()).isEqualTo(subject);
    }

    @Test
    public void generateTokenContainsCorrectIssuer() {
        // given
        String subject = "test@example.com";
        Map<String, Object> claims = new HashMap<>();

        // when
        String token = sut.generateToken(subject, claims);

        // then
        Claims parsedClaims = parseToken(token);
        assertThat(parsedClaims.getIssuer()).isEqualTo(testIssuer);
    }

    @Test
    public void generateTokenContainsCorrectExpirationTime() {
        // given
        String subject = "test@example.com";
        Map<String, Object> claims = new HashMap<>();
        Instant beforeGeneration = Instant.now();

        // when
        String token = sut.generateToken(subject, claims);

        // then
        Claims parsedClaims = parseToken(token);
        Instant expectedExpiry = beforeGeneration.plusSeconds(testExpirySeconds);
        
        assertThat(parsedClaims.getExpiration().toInstant())
                .isCloseTo(expectedExpiry, within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void generateTokenContainsCorrectIssuedAt() {
        // given
        String subject = "test@example.com";
        Map<String, Object> claims = new HashMap<>();
        Instant beforeGeneration = Instant.now();

        // when
        String token = sut.generateToken(subject, claims);

        // then
        Claims parsedClaims = parseToken(token);
        
        assertThat(parsedClaims.getIssuedAt().toInstant())
                .isCloseTo(beforeGeneration, within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void generateTokenContainsProvidedClaims() {
        // given
        String subject = "test@example.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", subject);
        claims.put("role", "ADMIN");
        claims.put("department", "IT");

        // when
        String token = sut.generateToken(subject, claims);

        // then
        Claims parsedClaims = parseToken(token);
        assertThat(parsedClaims.get("email", String.class)).isEqualTo(subject);
        assertThat(parsedClaims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(parsedClaims.get("department", String.class)).isEqualTo("IT");
    }

    @Test
    public void generateTokenWithEmptyClaimsWorks() {
        // given
        String subject = "test@example.com";
        Map<String, Object> claims = new HashMap<>();

        // when
        String token = sut.generateToken(subject, claims);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        
        Claims parsedClaims = parseToken(token);
        assertThat(parsedClaims.getSubject()).isEqualTo(subject);
    }

    @Test
    public void generateTokenWithNullClaimsWorks() {
        // given
        String subject = "test@example.com";
        Map<String, Object> claims = null;

        // when
        String token = sut.generateToken(subject, claims);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        
        Claims parsedClaims = parseToken(token);
        assertThat(parsedClaims.getSubject()).isEqualTo(subject);
    }

    @Test
    public void generateTokenWithDifferentSubjectsProducesDifferentTokens() {
        // given
        String subject1 = "user1@example.com";
        String subject2 = "user2@example.com";
        Map<String, Object> claims = new HashMap<>();

        // when
        String token1 = sut.generateToken(subject1, claims);
        String token2 = sut.generateToken(subject2, claims);

        // then
        assertThat(token1).isNotEqualTo(token2);
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(testSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
