package com.micropay.security.controller;

import com.micropay.security.dto.request.RegisterRequest;
import com.micropay.security.dto.response.AuthResponse;
import com.micropay.security.service.security.UserAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthenticationService userAuthenticationService   ;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = userAuthenticationService
                .registerUser(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<AuthResponse> refreshAccessToken(@RequestHeader ("X-User-Id") UUID userId) {
        AuthResponse response = userAuthenticationService
                .refreshAccessToken(userId);
        return ResponseEntity.ok(response);
    }

}
