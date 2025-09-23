package com.micropay.security.service.security;

import com.micropay.security.model.RoleType;

import java.util.UUID;

public interface JwtService {

    String generateAccessToken(UUID userId, RoleType role);

    String generateRefreshToken(UUID userId);

}
