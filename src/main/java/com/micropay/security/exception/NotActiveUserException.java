package com.micropay.security.exception;

import java.util.UUID;

public class NotActiveUserException extends RuntimeException {

    public NotActiveUserException(UUID userId) {
        super("User is not active. UserId: " + userId);
    }

    public NotActiveUserException(String message) {
        super(message);
    }

    public NotActiveUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
