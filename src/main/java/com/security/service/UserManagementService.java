package com.security.service;

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
    public void updateUserData(UUID userId, UserModel userModel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        user.setFullName(userModel.getFullName());
        user.setEmail(userModel.getEmail());

        userRepository.save(user);
    }

}
