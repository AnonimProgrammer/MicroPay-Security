package com.micropay.security.service.user.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.micropay.security.dto.request.UpdateUserRequest;
import com.micropay.security.dto.request.UserWalletRequest;
import com.micropay.security.dto.response.CursorPage;
import com.micropay.security.dto.response.UserResponse;
import com.micropay.security.dto.response.UserWalletResponse;
import com.micropay.security.exception.NotActiveUserException;
import com.micropay.security.exception.UserNotFoundException;
import com.micropay.security.mapper.UserMapper;
import com.micropay.security.model.UserModel;
import com.micropay.security.model.UserStatus;
import com.micropay.security.model.entity.User;
import com.micropay.security.repo.UserRepository;
import com.micropay.security.service.adapter.WalletServiceAdapter;
import com.micropay.security.service.cache.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserManagementServiceImplTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private WalletServiceAdapter walletServiceAdapter;
    private CacheService cacheService;

    private UserManagementServiceImpl userManagementService;

    private final static UUID USER_ID = UUID.randomUUID();
    private User user;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        walletServiceAdapter = mock(WalletServiceAdapter.class);
        cacheService = mock(CacheService.class);

        userManagementService = new UserManagementServiceImpl(
                userRepository, userMapper, walletServiceAdapter, cacheService
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
        user.setFullName("Omar Ismailov");
        user.setEmail("test@gmail.com");
    }

    @Test
    void getUserData_ShouldReturnCachedValue_WhenCacheHit() {
        UserResponse cachedResponse = mock(UserResponse.class);
        when(cacheService.getOrPut(
                eq("userData"), eq(USER_ID.toString()), any(TypeReference.class), any()))
                .thenReturn(cachedResponse);

        UserResponse response = userManagementService.getUserData(USER_ID);
        assertEquals(cachedResponse, response);
        verify(cacheService, times(1))
                .getOrPut(eq("userData"), eq(USER_ID.toString()), any(TypeReference.class), any());
    }

    @Test
    void updateUserData_ShouldUpdateFullNameAndEmail_WhenProvided() {
        UpdateUserRequest req = new UpdateUserRequest("Omar Ismailov", "omar@icloud.com");
        UserResponse updatedResponse = new UserResponse(
                null, "Omar Ismailov", "omar@icloud.com", null);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(updatedResponse);

        UserResponse result = userManagementService.updateUserData(USER_ID, req);

        assertEquals("Omar Ismailov", result.fullName());
        assertEquals("omar@icloud.com", result.email());
        verify(cacheService).evict("userData", USER_ID.toString());
        verify(cacheService).evictAll("users");
    }

    @Test
    void blockUser_ShouldBlockUser_AndCloseWallet() {
        user.setStatus(UserStatus.ACTIVE);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userManagementService.blockUser(USER_ID);

        assertEquals(UserStatus.BLOCKED, user.getStatus());
        verify(walletServiceAdapter).closeWallet(USER_ID);
        verify(cacheService).evict("userData", USER_ID.toString());
        verify(cacheService).evictAll("users");
        verify(userRepository).save(user);
    }

    @Test
    void activateUser_ShouldActivateUser_AndOpenWallet() {
        user.setStatus(UserStatus.SUSPENDED);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userManagementService.activateUser(USER_ID);

        assertEquals(UserStatus.ACTIVE, user.getStatus());
        verify(walletServiceAdapter).activateWallet(USER_ID);
        verify(userRepository).save(user);
    }

    @Test
    void suspendUser_ShouldSuspendUser_AndDeactivateWallet() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userManagementService.suspendUser(USER_ID);

        assertEquals(UserStatus.SUSPENDED, user.getStatus());
        verify(walletServiceAdapter).deactivateWallet(USER_ID);
        verify(userRepository).save(user);
    }

    @Test
    void getUserWalletId_ShouldReturnWalletId_WhenUserIsActive() {
        when(userRepository.findByPhoneNumber("123")).thenReturn(Optional.of(user));
        when(walletServiceAdapter.getWalletId(USER_ID)).thenReturn(55L);

        UserWalletResponse response = userManagementService
                .getUserWalletId(new UserWalletRequest("123"));

        assertEquals(55L, response.walletId());
        verify(walletServiceAdapter).getWalletId(USER_ID);
    }

    @Test
    void getUserWalletId_ShouldThrowException_WhenUserIsNotActive() {
        user.setStatus(UserStatus.BLOCKED);
        when(userRepository.findByPhoneNumber("123")).thenReturn(Optional.of(user));

        assertThrows(NotActiveUserException.class,
                () -> userManagementService.getUserWalletId(new UserWalletRequest("123")));
    }

    @Test
    void getUsers_ShouldReturnCursorPage_WhenUsersExist() {
        LocalDateTime now = LocalDateTime.now();
        List<User> users = List.of(user);
        UserModel userModel = mock(UserModel.class);

        when(userRepository.findUsers(any(), anyInt(), any(), anyString())).thenReturn(users);
        when(userMapper.toModel(any(User.class))).thenReturn(userModel);

        when(cacheService.getOrPut(eq("users"), anyString(), any(TypeReference.class), any()))
                .thenAnswer(invocation -> (
                        (Supplier<CursorPage<UserModel>>) invocation.getArgument(3)
                ).get());

        CursorPage<UserModel> result = userManagementService
                .getUsers(UserStatus.ACTIVE, 10, now, "asc");

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
    }

    @Test
    void getUsers_ShouldThrowUserNotFound_WhenEmpty() {
        LocalDateTime now = LocalDateTime.now();
        when(userRepository.findUsers(any(), anyInt(), any(), anyString())).thenReturn(List.of());
        when(cacheService.getOrPut(eq("users"), anyString(), any(TypeReference.class), any()))
                .thenAnswer(invocation -> (
                        (Supplier<?>) invocation.getArgument(3)
                ).get());

        assertThrows(UserNotFoundException.class,
                () -> userManagementService.getUsers(UserStatus.ACTIVE, 10, now, "asc"));
    }
}
