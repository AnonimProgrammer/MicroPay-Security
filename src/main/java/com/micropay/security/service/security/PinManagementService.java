package com.micropay.security.service.security;

import java.util.UUID;

public interface PinManagementService {

    String hashPin(String pin);

    void checkPinMatching(String rawPin, String hashedPin);

    void verifyPin(UUID userId, String pin);

    void updatePin(UUID userId, String newPin);
}
