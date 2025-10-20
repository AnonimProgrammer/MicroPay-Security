package com.micropay.security.service.security.impl;

import com.micropay.security.exception.CredentialNotFoundException;
import com.micropay.security.model.entity.Credential;
import com.micropay.security.repo.CredentialRepository;
import com.micropay.security.service.security.PinManagementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PinManagementServiceImpl implements PinManagementService {

    private final PasswordEncoder passwordEncoder;
    private final CredentialRepository credentialRepository;

    @Override
    public String hashPin(String pin) {
        return passwordEncoder.encode(pin);
    }

    @Override
    public void checkPinMatching(String rawPin, String hashedPin) {
        if (!passwordEncoder.matches(rawPin, hashedPin)) {
            throw new BadCredentialsException("Invalid PIN.");
        }
    }

    @Override
    public void verifyPin(UUID userId, String pin) {
        Credential credential = credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new CredentialNotFoundException(userId));

        checkPinMatching(pin, credential.getPinHash());
    }

    @Override
    @Transactional
    public void updatePin(UUID userId, String newPin) {
        Credential credential = credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new CredentialNotFoundException(userId));

        if (passwordEncoder.matches(newPin, credential.getPinHash())) {
            return;
        }
        credential.setPinHash(hashPin(newPin));
        credentialRepository.save(credential);
    }
}
