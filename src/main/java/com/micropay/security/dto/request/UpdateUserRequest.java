package com.micropay.security.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters.")
    String fullName,

    @Email(message = "Invalid email address.")
    String email
) { }
