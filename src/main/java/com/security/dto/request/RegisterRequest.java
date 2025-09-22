package com.security.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull(message = "Phone number is required.")
    @Pattern(
            regexp = "^\\+\\d{1,3}-\\d{2}-\\d{3}-\\d{2}-\\d{2}$",
            message = "Phone number must follow format +XXX-XX-XXX-XX-XX"
    )
    private String phoneNumber;

    @NotBlank(message = "Full name is required.")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters.")
    private String fullName;

    @Email(message = "Invalid email address.")
    private String email;

    @NotNull(message = "PIN is required.")
    @Pattern(
            regexp = "\\d{6}",
            message = "PIN must consist of exactly 6 digits."
    )
    private String pin;
}
