package com.micropay.security.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        String phoneNumber,
        String fullName,
        String email,
        LocalDateTime createdAt
) {}
