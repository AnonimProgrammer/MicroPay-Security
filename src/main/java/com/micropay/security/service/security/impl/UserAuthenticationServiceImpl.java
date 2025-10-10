package com.micropay.security.service.security.impl;

import com.micropay.security.dto.request.RegisterRequest;
import com.micropay.security.dto.response.AuthResponse;
import com.micropay.security.exception.*;
import com.micropay.security.mapper.CredentialMapper;
import com.micropay.security.mapper.UserMapper;
import com.micropay.security.model.CustomUserDetails;
import com.micropay.security.model.UserStatus;
import com.micropay.security.model.entity.Credential;
import com.micropay.security.model.entity.Role;
import com.micropay.security.model.entity.User;
import com.micropay.security.repo.CredentialRepository;
import com.micropay.security.repo.RoleRepository;
import com.micropay.security.repo.UserRepository;
import com.micropay.security.service.cache.CacheService;
import com.micropay.security.service.security.JwtService;
import com.micropay.security.service.security.PinManagementService;
import com.micropay.security.service.security.UserAuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final JwtService jwtService;
    private final PinManagementService pinManagementService;
    private final CacheService cacheService;

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;

    private final UserMapper userMapper;
    private final CredentialMapper credentialMapper;

    @Override
    @Transactional
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        final String phoneNumber = registerRequest.phoneNumber();

        log.info("Registering user with phone number: {}", phoneNumber);
        checkAccountExistence(phoneNumber);

        Role role = roleRepository.findById(1)
                .orElseThrow(() -> new InvalidRoleException("Default USER role not found."));

        User user = userMapper.buildEntity(registerRequest, role);
        String pinHash = pinManagementService.hashPin(registerRequest.pin());
        Credential credential = credentialMapper.buildEntity(user, pinHash);

        userRepository.save(user);
        credentialRepository.save(credential);

        cacheService.evictAll("users");
        return jwtService.generateTokens(user);
    }

    private void checkAccountExistence(String phoneNumber) {
        userRepository.findByPhoneNumber(phoneNumber).ifPresent(user -> {
                    throw new DuplicateObjectException(
                            "Account with phone number: " + phoneNumber + " already exists."
                    );
                });
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        log.info("Loading user with phone number: {}", phoneNumber);

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("No user found for the phone number: " + phoneNumber));

        Credential credential = credentialRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CredentialNotFoundException(user.getId()));

        isActive(user);
        return new CustomUserDetails(user, credential.getPinHash());
    }

    @Override
    public AuthResponse refreshAccessToken(UUID userId) {
        log.info("Refreshing access token for user: {}", userId);

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

}

