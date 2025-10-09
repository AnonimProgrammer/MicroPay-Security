package com.micropay.security.service.security.impl;

import com.micropay.security.dto.response.AuthResponse;
import com.micropay.security.model.RoleType;
import com.micropay.security.model.entity.Role;
import com.micropay.security.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("matrixVeryStrongKey123456789101112131415161718".getBytes());
    private static final long ACCESS_VALIDITY = 1000 * 60 * 5;
    private static final long REFRESH_VALIDITY = 1000 * 60 * 60;

    private JwtServiceImpl jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(SECRET, ACCESS_VALIDITY, REFRESH_VALIDITY);
        Role role = new Role();
        role.setRole(RoleType.ADMIN);

        user = new User();
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, UUID.randomUUID());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        user.setRole(role);
    }

    @Test
    void generateTokens_ShouldReturnValidAuthResponse() {
        AuthResponse response = jwtService.generateTokens(user);

        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertNotEquals(response.accessToken(), response.refreshToken());
    }

    @Test
    void accessToken_ShouldContainCorrectClaims() {
        AuthResponse response = jwtService.generateTokens(user);
        Claims claims = extractClaims(response.accessToken());

        assertEquals(user.getId().toString(), claims.getSubject());
        assertEquals(user.getRole().getRole().name(), claims.get("role"));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void refreshToken_ShouldHaveLaterExpirationThanAccessToken() {
        AuthResponse response = jwtService.generateTokens(user);

        Claims accessClaims = extractClaims(response.accessToken());
        Claims refreshClaims = extractClaims(response.refreshToken());

        assertTrue(refreshClaims.getExpiration().after(accessClaims.getExpiration()));
    }

    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
