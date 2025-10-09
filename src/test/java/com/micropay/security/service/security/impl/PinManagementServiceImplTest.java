package com.micropay.security.service.security.impl;

import com.micropay.security.exception.CredentialNotFoundException;
import com.micropay.security.model.entity.Credential;
import com.micropay.security.repo.CredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PinManagementServiceImplTest {

    private PasswordEncoder passwordEncoder;
    private CredentialRepository credentialRepository;
    private PinManagementServiceImpl pinService;

    private final UUID USER_ID = UUID.randomUUID();
    private final String RAW_PIN = "1234";
    private final String HASHED_PIN = "hashedPIN1234";

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        credentialRepository = mock(CredentialRepository.class);
        pinService = new PinManagementServiceImpl(passwordEncoder, credentialRepository);
    }

    @Test
    void hashPin_ShouldReturnEncodedPin() {
        when(passwordEncoder.encode(RAW_PIN)).thenReturn(HASHED_PIN);

        String result = pinService.hashPin(RAW_PIN);

        assertEquals(HASHED_PIN, result);
        verify(passwordEncoder, times(1)).encode(RAW_PIN);
    }

    @Test
    void checkPinMatching_ShouldPass_WhenPinsMatch() {
        when(passwordEncoder.matches(RAW_PIN, HASHED_PIN)).thenReturn(true);

        assertDoesNotThrow(() -> pinService.checkPinMatching(RAW_PIN, HASHED_PIN));
        verify(passwordEncoder, times(1)).matches(RAW_PIN, HASHED_PIN);
    }

    @Test
    void checkPinMatching_ShouldThrow_WhenPinsDoNotMatch() {
        when(passwordEncoder.matches(RAW_PIN, HASHED_PIN)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () ->
                pinService.checkPinMatching(RAW_PIN, HASHED_PIN));
    }

    @Test
    void verifyPin_ShouldValidateSuccessfully_WhenCredentialExistsAndMatches() {
        Credential credential = new Credential();
        credential.setPinHash(HASHED_PIN);

        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches(RAW_PIN, HASHED_PIN)).thenReturn(true);

        assertDoesNotThrow(() -> pinService.verifyPin(USER_ID, RAW_PIN));

        verify(credentialRepository, times(1)).findByUserId(USER_ID);
        verify(passwordEncoder, times(1)).matches(RAW_PIN, HASHED_PIN);
    }

    @Test
    void verifyPin_ShouldThrow_WhenCredentialNotFound() {
        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThrows(CredentialNotFoundException.class, () -> pinService.verifyPin(USER_ID, RAW_PIN));
    }

    @Test
    void verifyPin_ShouldThrow_WhenPinDoesNotMatch() {
        Credential credential = new Credential();
        credential.setPinHash(HASHED_PIN);

        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches(RAW_PIN, HASHED_PIN)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> pinService.verifyPin(USER_ID, RAW_PIN));
    }

    @Test
    void updatePin_ShouldUpdateHash_WhenPinIsDifferent() {
        Credential credential = new Credential();
        credential.setPinHash("oldHash");

        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches(RAW_PIN, credential.getPinHash())).thenReturn(false);
        when(passwordEncoder.encode(RAW_PIN)).thenReturn(HASHED_PIN);

        pinService.updatePin(USER_ID, RAW_PIN);

        assertEquals(HASHED_PIN, credential.getPinHash());
        verify(credentialRepository, times(1)).save(credential);
    }

    @Test
    void updatePin_ShouldNotUpdate_WhenNewPinMatchesOld() {
        Credential credential = new Credential();
        credential.setPinHash(HASHED_PIN);

        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches(RAW_PIN, HASHED_PIN)).thenReturn(true);

        pinService.updatePin(USER_ID, RAW_PIN);

        verify(credentialRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updatePin_ShouldThrow_WhenCredentialNotFound() {
        when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThrows(CredentialNotFoundException.class, () -> pinService.updatePin(USER_ID, HASHED_PIN));
    }
}
