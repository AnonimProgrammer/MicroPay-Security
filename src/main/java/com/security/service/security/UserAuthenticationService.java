package com.security.service.security;

import com.security.dto.request.RegisterRequest;
import com.security.dto.response.AuthResponse;
import com.security.model.CustomUserDetails;
import com.security.model.RoleType;
import com.security.model.UserStatus;
import com.security.model.entity.Credential;
import com.security.model.entity.Role;
import com.security.model.entity.User;
import com.security.repo.CredentialRepository;
import com.security.repo.RoleRepository;
import com.security.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService implements UserDetailsService {

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
                .phoneNumber(registerRequest.getPhoneNumber())
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        String pinHash = pinManagementService.hashPin(registerRequest.getPin());

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

        return new CustomUserDetails(user, credential.getPinHash());
    }
}

