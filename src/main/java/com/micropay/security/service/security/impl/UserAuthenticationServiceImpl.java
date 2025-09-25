package com.micropay.security.service.security.impl;

import com.micropay.security.dto.request.RegisterRequest;
import com.micropay.security.dto.response.AuthResponse;
import com.micropay.security.exception.CredentialNotFoundException;
import com.micropay.security.exception.InvalidRoleException;
import com.micropay.security.exception.NotActiveUserException;
import com.micropay.security.exception.UserNotFoundException;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final JwtService jwtService;
    private final PinManagementService pinManagementService;
    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        Role role = roleRepository.findByRole(RoleType.USER)
                .orElseThrow(() -> new InvalidRoleException("No such role: " + RoleType.USER));

        User user = buildUser(registerRequest, role);
        userRepository.save(user);

        String pinHash = pinManagementService.hashPin(registerRequest.pin());

        Credential credential = buildCredential(user, pinHash);
        credentialRepository.save(credential);

        return jwtService.generateTokens(user);
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException(
                        "No user found for the phone number: " + phoneNumber
                        ));

        Credential credential = credentialRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CredentialNotFoundException(user.getId()));

        isActive(user);
        return new CustomUserDetails(user, credential.getPinHash());
    }

    @Override
    public AuthResponse refreshAccessToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        isActive(user);
        return jwtService.generateTokens(user);
    }

    private void isActive(User user) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new NotActiveUserException(user.getId());
        }
    }

    private User buildUser(RegisterRequest registerRequest, Role role) {
        return new User.Builder()
                .phoneNumber(registerRequest.phoneNumber())
                .fullName(registerRequest.fullName())
                .email(registerRequest.email())
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private Credential buildCredential(User user, String pinHash) {
        return new Credential.Builder()
                .user(user)
                .pinHash(pinHash)
                .withBiometricEnabled(false)
                .build();
    }
}

