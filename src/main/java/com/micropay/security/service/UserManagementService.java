package com.micropay.security.service;

import com.micropay.security.dto.request.UpdateUserRequest;
import com.micropay.security.model.UserModel;
import com.micropay.security.model.UserStatus;

import java.util.UUID;

public interface UserManagementService {

    UserModel getUserData(UUID userId);

    UserModel updateUserData(UUID userId, UpdateUserRequest updateUserRequest);

    void updateUserStatus(UUID userId, UserStatus userStatus);

}
