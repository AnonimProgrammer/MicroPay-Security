package com.micropay.security.service.impl;

import com.micropay.security.dto.request.UpdateUserRequest;
import com.micropay.security.exception.UserNotFoundException;
import com.micropay.security.mapper.Mapper;
import com.micropay.security.model.UserModel;
import com.micropay.security.model.UserStatus;
import com.micropay.security.model.entity.User;
import com.micropay.security.repo.UserRepository;
import com.micropay.security.service.UserManagementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final Mapper<User, UserModel> userMapper;

    @Override
    public UserModel getUserData(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return userMapper.map(user);
    }

    @Override
    @Transactional
    public UserModel updateUserData(UUID userId, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (updateUserRequest.fullName() != null) {
            user.setFullName(updateUserRequest.fullName());
        }
        if (updateUserRequest.email() != null) {
            user.setEmail(updateUserRequest.email());
        }
        return userMapper.map(userRepository.save(user));
    }

    @Override
    @Transactional
    public void updateUserStatus(UUID userId, UserStatus userStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setStatus(userStatus);
        userRepository.save(user);
    }

}
