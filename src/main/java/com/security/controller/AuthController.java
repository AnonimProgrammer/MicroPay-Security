package com.security.controller;

import com.security.dto.request.RegisterRequest;
import com.security.dto.response.AuthResponse;
import com.security.service.security.UserAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthenticationService userAuthenticationService   ;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = userAuthenticationService.registerUser(registerRequest);
        return ResponseEntity.ok(response);
    }

}
