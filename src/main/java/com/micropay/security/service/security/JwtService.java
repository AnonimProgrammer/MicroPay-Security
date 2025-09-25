package com.micropay.security.service.security;

import com.micropay.security.dto.response.AuthResponse;
import com.micropay.security.model.entity.User;

public interface JwtService {

    AuthResponse generateTokens(User user);

}
