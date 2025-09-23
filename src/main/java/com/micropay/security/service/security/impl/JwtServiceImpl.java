package com.micropay.security.service.security.impl;

import com.micropay.security.model.RoleType;
import com.micropay.security.service.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtServiceImpl implements JwtService {

    private final String secretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtServiceImpl(
            @Value("${security.jwt.secret}") String secretKey,
            @Value("${security.jwt.access-token-validity}") long accessTokenValidity,
            @Value("${security.jwt.refresh-token-validity}") long refreshTokenValidity
    ) {
        this.secretKey = secretKey;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    @Override
    public String generateAccessToken(UUID userId, RoleType role) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(getKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
