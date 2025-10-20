package com.micropay.security.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Invalid token.");
    }

    public InvalidTokenException(String message) {
        super(message);
    }

}
