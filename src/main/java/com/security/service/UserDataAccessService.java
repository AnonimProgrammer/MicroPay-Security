package com.security.service;

import com.security.dto.request.RegisterRequest;
import com.security.dto.response.AuthResponse;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDataAccessService {

    private final JwtService jwtService;
    private final PinService pinService;
    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        Role role = roleRepository.findByRole(RoleType.USER)
                .orElseThrow(() -> new RuntimeException("Role not found."));

        User user = new User();
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        String pinHash = pinService.hashPin(registerRequest.getPin());

        Credential credential = new Credential();
        credential.setUser(user);
        credential.setPinHash(pinHash);
        credentialRepository.save(credential);

        return generateTokens(user);
    }

    public AuthResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole().getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken);
    }

}
