package com.security.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthRequest {

    @NotNull(message = "Phone number is required.")
    @Pattern(
            regexp = "^\\+\\d{1,3}-\\d{2}-\\d{3}-\\d{2}-\\d{2}$",
            message = "Phone number must follow format +XXX-XX-XXX-XX-XX"
    )
    private String phoneNumber;

    @NotNull(message = "PIN is required.")
    @Pattern(
            regexp = "\\d{6}",
            message = "PIN must consist of exactly 6 digits."
    )
    private String pin;
}