package com.micropay.security.exception;

import java.util.UUID;

public class CredentialNotFoundException extends RuntimeException {

    public CredentialNotFoundException(UUID userId) {
        super("Credentials not found for userId: " + userId);
    }

}
