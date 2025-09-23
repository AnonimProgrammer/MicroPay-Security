package com.micropay.security.exception;

import java.util.UUID;

public class CredentialNotFoundException extends RuntimeException {

    public CredentialNotFoundException(UUID userId) {
        super("Credentials not found for userId: " + userId);
    }

    public CredentialNotFoundException(String message) {
        super(message);
    }

    public CredentialNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
