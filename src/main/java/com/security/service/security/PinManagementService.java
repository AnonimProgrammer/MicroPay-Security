package com.security.service.security;

import com.security.model.entity.Credential;
import com.security.repo.CredentialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PinManagementService {

    private final PasswordEncoder passwordEncoder;
    private final CredentialRepository credentialRepository;

    public String hashPin(String pin) {
        return passwordEncoder.encode(pin);
    }

    public void checkPinMatching(String rawPin, String hashedPin) {
        if (!passwordEncoder.matches(rawPin, hashedPin)) {
            throw new BadCredentialsException("Passwords do not match.");
        }
    }

    public void verifyPin(UUID userId, String pin) {
        Credential credential = credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Credentials not found."));

        checkPinMatching(pin, credential.getPinHash());
    }

    @Transactional
    public void updatePin(UUID userId, String newPin) {
        Credential credential = credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Credentials not found."));

        String hashedPin = hashPin(newPin);
        credential.setPinHash(hashedPin);

        credentialRepository.save(credential);
    }
}
