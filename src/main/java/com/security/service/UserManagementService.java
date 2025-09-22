package com.security.service;

import com.security.dto.request.UpdateUserRequest;
import com.security.mapper.Mapper;
import com.security.model.UserModel;
import com.security.model.entity.User;
import com.security.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final Mapper<User, UserModel> userMapper;

    public UserModel getUserData(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        return userMapper.map(user);
    }

    @Transactional
    public void updateUserData(UUID userId, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (updateUserRequest.fullName() != null) {
            user.setFullName(updateUserRequest.fullName());
        }
        if (updateUserRequest.email() != null) {
            user.setEmail(updateUserRequest.email());
        }
        userRepository.save(user);
    }

}
