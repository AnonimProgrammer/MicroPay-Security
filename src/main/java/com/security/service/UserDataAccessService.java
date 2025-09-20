package com.security.service;

import com.security.dto.request.RegisterRequest;
import com.security.dto.response.AuthResponse;
import com.security.mapper.Mapper;
import com.security.model.CustomUserDetails;
import com.security.model.RoleType;
import com.security.model.UserModel;
import com.security.model.UserStatus;
import com.security.model.entity.Credential;
import com.security.model.entity.Role;
import com.security.model.entity.User;
import com.security.repo.CredentialRepository;
import com.security.repo.RoleRepository;
import com.security.repo.UserRepository;
import com.security.service.security.JwtService;
import com.security.service.security.PinService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDataAccessService {

    private final JwtService jwtService;
    private final PinService pinService;
    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;
    private final Mapper<User, UserModel> userMapper;

    @Transactional
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        Role role = roleRepository.findByRole(RoleType.USER)
                .orElseThrow(() -> new RuntimeException("Role not found."));

        User user = new User.Builder()
                .phoneNumber(registerRequest.getPhoneNumber())
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        String pinHash = pinService.hashPin(registerRequest.getPin());

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

    public UserDetails loadByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found."));
        Credential credential = credentialRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Credentials not found."));

        return new CustomUserDetails(user, credential.getPinHash());
    }

    public UserModel getUserData(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        return userMapper.map(user);
    }

    @Transactional
    public void updateUserData(UUID userId, UserModel userModel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        user.setFullName(userModel.getFullName());
        user.setEmail(userModel.getEmail());

        userRepository.save(user);
    }

}
