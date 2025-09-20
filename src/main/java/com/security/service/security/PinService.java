package com.security.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinService {

    private final PasswordEncoder passwordEncoder;

    public String hashPin(String pin) {
        return passwordEncoder.encode(pin);
    }

    public boolean verifyPin(String rawPin, String hashedPin) {
        return passwordEncoder.matches(rawPin, hashedPin);
    }
}
