package com.micropay.security.controller;

import com.micropay.security.dto.request.RegisterRequest;
import com.micropay.security.dto.response.AuthResponse;
import com.micropay.security.service.security.UserAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthenticationService userAuthenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = userAuthenticationService
                .registerUser(registerRequest);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<AuthResponse> refreshAccessToken(
            @RequestHeader ("X-Refresh-Token") String refreshToken
    ) {
        AuthResponse response = userAuthenticationService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }

}
