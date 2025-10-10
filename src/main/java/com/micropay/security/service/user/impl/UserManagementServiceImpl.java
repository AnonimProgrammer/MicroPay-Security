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
import com.micropay.security.service.user.UserManagementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WalletServiceAdapter walletServiceAdapter;
    private final CacheService cacheService;

    private final static int DEFAULT_PAGE_SIZE = 20;

    @Override
    public UserResponse getUserData(UUID userId) {
        log.info("[UserManagementService] - Fetching user data for userId: {}", userId);

        String cacheKey = userId.toString();
        return cacheService.getOrPut(
                "userData", cacheKey, new TypeReference<UserResponse>() {}, () -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException(userId));
                    return userMapper.toResponse(user);
                }
        );
    }

    @Override
    @Transactional
    public UserResponse updateUserData(UUID userId, UpdateUserRequest updateUserRequest) {
        log.info("[UserManagementService] - Updating user data for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (updateUserRequest.fullName() != null) {
            user.setFullName(updateUserRequest.fullName());
        }
        if (updateUserRequest.email() != null) {
            user.setEmail(updateUserRequest.email());
        }
        User savedUser = userRepository.save(user);
        evictCaches(userId);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public void blockUser(UUID userId) {
        log.info("[UserManagementService] - Blocking user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setStatus(UserStatus.BLOCKED);
        walletServiceAdapter.closeWallet(userId);

        userRepository.save(user);
        evictCaches(userId);
    }

    @Override
    @Transactional
    public void activateUser(UUID userId) {
        log.info("[UserManagementService] - Activating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setStatus(UserStatus.ACTIVE);
        walletServiceAdapter.activateWallet(userId);

        userRepository.save(user);
        evictCaches(userId);
    }

    @Override
    @Transactional
    public void suspendUser(UUID userId) {
        log.info("[UserManagementService] - Suspending user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setStatus(UserStatus.SUSPENDED);
        walletServiceAdapter.deactivateWallet(userId);

        userRepository.save(user);
        evictCaches(userId);
    }

    private void evictCaches(UUID userId) {
        cacheService.evict("userData", userId.toString());
        cacheService.evictAll("users");
    }

    @Override
    public UserWalletResponse getUserWalletId(UserWalletRequest userWalletRequest) {
        log.info("[UserManagementService] - Fetching walletId for phoneNumber: {}", userWalletRequest.phoneNumber());

        User user = userRepository.findByPhoneNumber(userWalletRequest.phoneNumber())
                .orElseThrow(() -> new UserNotFoundException("User is not using MicroPay yet."));

        isActive(user);
        Long walletId = walletServiceAdapter.getWalletId(user.getId());
        return new UserWalletResponse(user.getId(), walletId);
    }

    private void isActive(User user) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new NotActiveUserException(user.getId());
        }
    }

    @Override
    public CursorPage<UserModel> getUsers(
            UserStatus status, Integer pageSize,
            LocalDateTime cursorDate, String sortBy
    ) {
        final int size = (pageSize == null || pageSize > 100) ? DEFAULT_PAGE_SIZE : pageSize;
        String sortOrder = (sortBy != null && sortBy.equalsIgnoreCase("asc")) ? "ASC" : "DESC";

        String cacheKey = buildCacheKey(status, cursorDate, sortOrder, size);
        return cacheService.getOrPut(
                "users",
                cacheKey, new TypeReference<CursorPage<UserModel>>() {}, () -> {
                    List<User> users = userRepository.findUsers(status, size, cursorDate, sortOrder);
                    checkIfEmpty(users);

                    boolean hasNext = users.size() > size;
                    LocalDateTime nextCursor = hasNext ? users.get(size).getCreatedAt() : null;

                    if (hasNext) {
                        users = users.subList(0, size);
                    }
                    List<UserModel> userModels = users.stream()
                            .map(userMapper::toModel)
                            .toList();

                    return new CursorPage<>(userModels, nextCursor, hasNext);
                }
        );
    }

    private String buildCacheKey(UserStatus status, LocalDateTime cursorDate, String sortOrder, Integer pageSize) {
        return String.format("%s_%s_%s_%s",
                status != null ? status.name() : "null",
                cursorDate != null ? cursorDate.toString() : "null",
                sortOrder,
                pageSize
        );
    }

    private void checkIfEmpty(List<User> users) {
        if (users.isEmpty()) {
            throw new UserNotFoundException("No wallets found in the system.");
        }
    }


}
