package com.security.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequest {

    @NotNull(message = "Phone number cannot be null")
    private String phoneNumber;

    @NotNull(message = "PIN cannot be null")
    private String pin;
}