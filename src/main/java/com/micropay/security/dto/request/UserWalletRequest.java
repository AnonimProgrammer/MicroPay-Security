package com.micropay.security.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserWalletRequest(
        @NotNull(message = "Phone number is required.")
        @Pattern(
                regexp = "^\\+\\d{1,3}-\\d{2}-\\d{3}-\\d{2}-\\d{2}$",
                message = "Phone number must follow format +XXX-XX-XXX-XX-XX"
        )
        String phoneNumber
) {}
