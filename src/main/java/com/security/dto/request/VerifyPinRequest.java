package com.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyPinRequest(
        @NotBlank(message = "PIN can not be blank.")
        @Size(message = "PIN must consist of 6 digits.", min = 6, max = 6)
        String pin
) {}

