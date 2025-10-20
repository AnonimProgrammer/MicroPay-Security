package com.micropay.security.service.security.impl;

import com.micropay.security.dto.request.RegisterRequest;
import com.micropay.security.dto.response.AuthResponse;
import com.micropay.security.exception.*;
import com.micropay.security.mapper.CredentialMapper;
import com.micropay.security.mapper.UserMapper;
import com.micropay.security.model.CustomUserDetails;
import com.micropay.security.model.RoleType;
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
    private final static UUID USER_ID = UUID.randomUUID();
    private final static String REFRESH_TOKEN = "sample-refresh-token";

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
        role.setRole(RoleType.USER);
        user.setRole(role);
        credential = new Credential();
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully() {
        when(userRepository.findByPhoneNumber(registerRequest.phoneNumber())).thenReturn(Optional.empty());

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
        when(userRepository.findByPhoneNumber(registerRequest.phoneNumber())).thenReturn(Optional.empty());
        when(roleRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(InvalidRoleException.class, () -> service.registerUser(registerRequest));
        verify(userRepository, never()).save(any());
        verify(credentialRepository, never()).save(any());
        verify(cacheService, never()).evictAll(anyString());
    }

    @Test
    void registerUser_ShouldThrow_WhenUserAlreadyExists() {
        when(userRepository.findByPhoneNumber(registerRequest.phoneNumber())).thenReturn(Optional.of(user));

        assertThrows(DuplicateObjectException.class, () -> service.registerUser(registerRequest));

        verify(userRepository, never()).save(any());
        verify(credentialRepository, never()).save(any());
        verify(cacheService, never()).evictAll(anyString());
        verify(jwtService, never()).generateTokens(any());
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
    void shouldRefreshAccessTokenSuccessfully() {
        UUID userId = user.getId();

        doNothing().when(cacheService).checkAndBlacklist(REFRESH_TOKEN);
        doNothing().when(jwtService).validateToken(REFRESH_TOKEN);
        when(jwtService.extractUserId(REFRESH_TOKEN)).thenReturn(userId.toString());
        when(jwtService.extractRole(REFRESH_TOKEN)).thenReturn("USER");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        AuthResponse expectedResponse = new AuthResponse("newAccessToken", "newREFRESH_TOKEN");
        when(jwtService.generateTokens(user)).thenReturn(expectedResponse);

        AuthResponse actual = service.refreshAccessToken(REFRESH_TOKEN);

        assertEquals(expectedResponse, actual);
        verify(cacheService).checkAndBlacklist(REFRESH_TOKEN);
        verify(jwtService).validateToken(REFRESH_TOKEN);
        verify(jwtService).extractUserId(REFRESH_TOKEN);
        verify(jwtService).extractRole(REFRESH_TOKEN);
        verify(userRepository).findById(userId);
        verify(jwtService).generateTokens(user);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenREFRESH_TOKENBlacklisted() {
        doThrow(new InvalidTokenException("Token blacklisted"))
                .when(cacheService).checkAndBlacklist(REFRESH_TOKEN);

        InvalidTokenException exception = assertThrows(
                InvalidTokenException.class, () -> service.refreshAccessToken(REFRESH_TOKEN)
        );
        assertEquals("Token blacklisted", exception.getMessage());
        verify(cacheService).checkAndBlacklist(REFRESH_TOKEN);
        verifyNoMoreInteractions(jwtService, userRepository);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenRoleMismatch() {
        UUID userId = user.getId();

        doNothing().when(cacheService).checkAndBlacklist(REFRESH_TOKEN);
        doNothing().when(jwtService).validateToken(REFRESH_TOKEN);
        when(jwtService.extractUserId(REFRESH_TOKEN)).thenReturn(userId.toString());
        when(jwtService.extractRole(REFRESH_TOKEN)).thenReturn("ADMIN");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        InvalidTokenException ex = assertThrows(
                InvalidTokenException.class, () -> service.refreshAccessToken(REFRESH_TOKEN)
        );
        assertEquals("Invalid refresh token.", ex.getMessage());
    }

    @Test
    void shouldThrowNotActiveUserExceptionWhenUserNotActive() {
        UUID userId = user.getId();
        user.setStatus(UserStatus.BLOCKED);

        doNothing().when(cacheService).checkAndBlacklist(REFRESH_TOKEN);
        doNothing().when(jwtService).validateToken(REFRESH_TOKEN);
        when(jwtService.extractUserId(REFRESH_TOKEN)).thenReturn(userId.toString());
        when(jwtService.extractRole(REFRESH_TOKEN)).thenReturn("USER");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        NotActiveUserException ex = assertThrows(
                NotActiveUserException.class, () -> service.refreshAccessToken(REFRESH_TOKEN)
        );
        assertTrue(ex.getMessage().contains(userId.toString()));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();

        doNothing().when(cacheService).checkAndBlacklist(REFRESH_TOKEN);
        doNothing().when(jwtService).validateToken(REFRESH_TOKEN);
        when(jwtService.extractUserId(REFRESH_TOKEN)).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(
                UserNotFoundException.class, () -> service.refreshAccessToken(REFRESH_TOKEN)
        );

        assertEquals("User not found with id: " + userId, ex.getMessage());
    }

}
