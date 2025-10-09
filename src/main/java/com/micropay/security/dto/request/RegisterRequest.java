package com.micropay.security.dto.request;

import jakarta.validation.constraints.*;

public record RegisterRequest(

        @NotNull(message = "Phone number is required.")
        @Pattern(
                regexp = "^\\+\\d{1,3}-\\d{2}-\\d{3}-\\d{2}-\\d{2}$",
                message = "Phone number must follow format +XXX-XX-XXX-XX-XX"
        )
        String phoneNumber,

        @NotBlank(message = "Full name is required.")
        @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters.")
        String fullName,

        @Email(
                message = "Invalid email address.",
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )
        String email,

        @NotNull(message = "PIN is required.")
        @Pattern(
                regexp = "\\d{6}",
                message = "PIN must consist of exactly 6 digits."
        )
        String pin
) {}
