package com.micropay.security.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends UsernameNotFoundException {

    public UserNotFoundException(UUID userId) {
        super("User not found with id: " + userId);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

}
