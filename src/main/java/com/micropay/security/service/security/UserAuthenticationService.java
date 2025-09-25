package com.micropay.security.service.security;

import com.micropay.security.dto.request.RegisterRequest;
import com.micropay.security.dto.response.AuthResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserAuthenticationService extends UserDetailsService {

    AuthResponse registerUser(RegisterRequest registerRequest);

    AuthResponse refreshAccessToken(UUID userId);

}
