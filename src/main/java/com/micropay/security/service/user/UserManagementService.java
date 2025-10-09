package com.micropay.security.service.user;

import com.micropay.security.dto.request.UpdateUserRequest;
import com.micropay.security.dto.request.UserWalletRequest;
import com.micropay.security.dto.response.CursorPage;
import com.micropay.security.dto.response.UserResponse;
import com.micropay.security.dto.response.UserWalletResponse;
import com.micropay.security.model.UserModel;
import com.micropay.security.model.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserManagementService {

    UserResponse getUserData(UUID userId);

    UserResponse updateUserData(UUID userId, UpdateUserRequest updateUserRequest);

    void blockUser(UUID userId);

    void activateUser(UUID userId);

    void suspendUser(UUID userId);

    UserWalletResponse getUserWalletId(UserWalletRequest userWalletRequest);

    CursorPage<UserModel> getUsers(
            UserStatus status, Integer pageSize,
            LocalDateTime cursorDate, String sortBy
    );
}
