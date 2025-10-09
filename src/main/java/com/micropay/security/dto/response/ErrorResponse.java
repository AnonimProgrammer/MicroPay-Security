package com.micropay.security.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        Integer status, String message, LocalDateTime timestamp
) {}
