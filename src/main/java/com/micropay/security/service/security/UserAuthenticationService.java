package com.micropay.security.service.security;

import com.micropay.security.dto.request.RegisterRequest;
import com.micropay.security.dto.response.AuthResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserAuthenticationService extends UserDetailsService {

    AuthResponse registerUser(RegisterRequest registerRequest);

    AuthResponse refreshAccessToken(String refreshToken);

}
