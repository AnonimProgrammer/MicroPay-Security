package com.micropay.security.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VerifyPinRequest(
        @NotNull(message = "PIN is required.")
        @Pattern(
                regexp = "\\d{6}",
                message = "PIN must consist of exactly 6 digits."
        )
        String pin
) {}

