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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthenticationServiceImplTest {

    private JwtService jwtService;
    private PinManagementService pinManagementService;
    private CacheService cacheService;
    private UserRepository userRepository;
    private CredentialRepository credentialRepository;
    private RoleRepository roleRepository;
    private UserMapper userMapper;
    private CredentialMapper credentialMapper;

    private UserAuthenticationServiceImpl service;

    private RegisterRequest registerRequest;
    private User user;
    private Role role;
    private Credential credential;
    private final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        pinManagementService = mock(PinManagementService.class);
        cacheService = mock(CacheService.class);
        userRepository = mock(UserRepository.class);
        credentialRepository = mock(CredentialRepository.class);
        roleRepository = mock(RoleRepository.class);
        userMapper = mock(UserMapper.class);
        credentialMapper = mock(CredentialMapper.class);

        service = new UserAuthenticationServiceImpl(
                jwtService, pinManagementService, cacheService,
                userRepository, credentialRepository, roleRepository,
                userMapper, credentialMapper
        );

        registerRequest = new RegisterRequest(
                "+994-55-111-11-11",
                "Omar Ismailov",
                "test@gmail.com",
                "1234"
        );
        user = new User();
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, USER_ID);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        user.setStatus(UserStatus.ACTIVE);
        role = new Role();
        credential = new Credential();
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully() {
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(userMapper.buildEntity(registerRequest, role)).thenReturn(user);
        when(pinManagementService.hashPin("1234")).thenReturn("hashed");
        when(credentialMapper.buildEntity(user, "hashed")).thenReturn(credential);
        when(jwtService.generateTokens(user)).thenReturn(new AuthResponse("access", "refresh"));

        AuthResponse response = service.registerUser(registerRequest);

        verify(userRepository).save(user);
        verify(credentialRepository).save(credential);
        verify(cacheService).evictAll("users");
        verify(jwtService).generateTokens(user);
        assertEquals("access", response.accessToken());
    }

    @Test
    void registerUser_ShouldThrow_WhenRoleNotFound() {
        when(roleRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(InvalidRoleException.class, () -> service.registerUser(registerRequest));
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserAndCredentialExist() {
        user.setStatus(UserStatus.ACTIVE);
        credential.setPinHash("hashedPin");

        when(userRepository.findByPhoneNumber("+994-55-111-11-11")).thenReturn(Optional.of(user));
        when(credentialRepository.findByUserId(user.getId())).thenReturn(Optional.of(credential));

        CustomUserDetails details = (CustomUserDetails) service.loadUserByUsername("+994-55-111-11-11");

        assertEquals("hashedPin", details.getPassword());
        verify(userRepository).findByPhoneNumber("+994-55-111-11-11");
        verify(credentialRepository).findByUserId(user.getId());
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.loadUserByUsername("+994"));
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenCredentialNotFound() {
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(credentialRepository.findByUserId(any())).thenReturn(Optional.empty());

        assertThrows(CredentialNotFoundException.class, () -> service.loadUserByUsername("+994"));
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenUserNotActive() {
        user.setStatus(UserStatus.BLOCKED);
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));
        when(credentialRepository.findByUserId(any())).thenReturn(Optional.of(credential));

        assertThrows(NotActiveUserException.class, () -> service.loadUserByUsername("+994"));
    }

    @Test
    void refreshAccessToken_ShouldReturnNewTokens_WhenUserActive() {
        user.setStatus(UserStatus.ACTIVE);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(jwtService.generateTokens(user)).thenReturn(
                new AuthResponse("newAccess", "newRefresh")
        );

        AuthResponse response = service.refreshAccessToken(USER_ID);

        assertEquals("newAccess", response.accessToken());
        verify(jwtService).generateTokens(user);
    }

    @Test
    void refreshAccessToken_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.refreshAccessToken(USER_ID));
    }

    @Test
    void refreshAccessToken_ShouldThrow_WhenUserNotActive() {
        user.setStatus(UserStatus.BLOCKED);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        assertThrows(NotActiveUserException.class, () -> service.refreshAccessToken(USER_ID));
    }
}
