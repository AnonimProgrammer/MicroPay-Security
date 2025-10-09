package com.micropay.security.controller;

import com.micropay.security.dto.request.UpdatePinRequest;
import com.micropay.security.dto.request.VerifyPinRequest;
import com.micropay.security.service.security.PinManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class PinManagementController {

    private final PinManagementService pinManagementService;

    @PutMapping("/pin")
    public ResponseEntity<Void> updatePin(
            @RequestHeader ("X-User-Id") UUID userId,
            @RequestBody @Valid UpdatePinRequest updatePinRequest
    ) {
        pinManagementService.updatePin(userId, updatePinRequest.newPin());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-pin")
    public ResponseEntity<Void> verifyPin(
            @RequestHeader ("X-User-Id") UUID userId,
            @RequestBody @Valid VerifyPinRequest verifyPinRequest
    ) {
        pinManagementService.verifyPin(userId, verifyPinRequest.pin());
        return ResponseEntity.noContent().build();
    }

}
