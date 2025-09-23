package com.micropay.security.service.security.impl;

import com.micropay.security.dto.request.RegisterRequest;
import com.micropay.security.dto.response.AuthResponse;
import com.micropay.security.model.CustomUserDetails;
import com.micropay.security.model.RoleType;
import com.micropay.security.model.UserStatus;
import com.micropay.security.model.entity.Credential;
import com.micropay.security.model.entity.Role;
import com.micropay.security.model.entity.User;
import com.micropay.security.repo.CredentialRepository;
import com.micropay.security.repo.RoleRepository;
import com.micropay.security.repo.UserRepository;
import com.micropay.security.service.security.JwtService;
import com.micropay.security.service.security.PinManagementService;
import com.micropay.security.service.security.UserAuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final JwtService jwtService;
    private final PinManagementService pinManagementService;
    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        Role role = roleRepository.findByRole(RoleType.USER)
                .orElseThrow(() -> new RuntimeException("Role not found."));

        User user = new User.Builder()
                .phoneNumber(registerRequest.phoneNumber())
                .fullName(registerRequest.fullName())
                .email(registerRequest.email())
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        String pinHash = pinManagementService.hashPin(registerRequest.pin());

        Credential credential = new Credential.Builder()
                .user(user)
                .pinHash(pinHash)
                .withBiometricEnabled(false)
                .build();
        credentialRepository.save(credential);

        return generateTokens(user);
    }

    public AuthResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole().getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        Credential credential = credentialRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Credentials not found."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("User is not active.");
        }

        return new CustomUserDetails(user, credential.getPinHash());
    }
}

