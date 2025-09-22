package com.security.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AuthRequest(

        @NotNull(message = "Phone number is required.")
        @Pattern(
                regexp = "^\\+\\d{1,3}-\\d{2}-\\d{3}-\\d{2}-\\d{2}$",
                message = "Phone number must follow format +XXX-XX-XXX-XX-XX"
        )
        String phoneNumber,

        @NotNull(message = "PIN is required.")
        @Pattern(
                regexp = "\\d{6}",
                message = "PIN must consist of exactly 6 digits."
        )
        String pin
) {}
