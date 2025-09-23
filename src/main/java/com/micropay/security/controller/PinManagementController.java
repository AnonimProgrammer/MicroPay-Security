package com.micropay.security.controller;

import com.micropay.security.dto.request.UpdatePinRequest;
import com.micropay.security.dto.request.VerifyPinRequest;
import com.micropay.security.service.security.PinManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PinManagementController {

    private final PinManagementService pinManagementService;

    @PutMapping("/pin")
    public void updatePin(
            @RequestHeader ("X-User-Id") UUID userId,
            @Valid @RequestBody UpdatePinRequest updatePinRequest
    ) {
        pinManagementService.updatePin(userId, updatePinRequest.newPin());
    }

    @PostMapping("/verify-pin")
    public void verifyPin(
            @RequestHeader ("X-User-Id") UUID userId,
            @Valid @RequestBody VerifyPinRequest verifyPinRequest
    ) {
        pinManagementService.verifyPin(userId, verifyPinRequest.pin());
    }

}
