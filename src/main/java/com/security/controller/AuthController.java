package com.security.controller;

import com.security.dto.request.AuthRequest;
import com.security.dto.request.RegisterRequest;
import com.security.dto.response.AuthResponse;
import com.security.service.UserDataAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserDataAccessService userDataAccessService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = userDataAccessService.registerUser(registerRequest);
        return ResponseEntity.ok(response);
    }

}
