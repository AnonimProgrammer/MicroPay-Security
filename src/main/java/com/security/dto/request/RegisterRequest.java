package com.security.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Full name is required")
    private String fullName;

    private String email;

    @NotNull(message = "PIN is required")
    private String pin;
}
