package com.micropay.security.controller;

import com.micropay.security.dto.request.UpdateUserRequest;
import com.micropay.security.dto.request.UserWalletRequest;
import com.micropay.security.dto.response.UserResponse;
import com.micropay.security.dto.response.UserWalletResponse;
import com.micropay.security.service.user.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userDataAccessService;

    @GetMapping
    public ResponseEntity<UserResponse> getUserData(@RequestHeader ("X-User-Id") UUID userId) {
        UserResponse user = userDataAccessService.getUserData(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/wallet-id")
    public ResponseEntity<UserWalletResponse> getUserWalletId(@RequestBody UserWalletRequest userWalletRequest) {
        UserWalletResponse response = userDataAccessService.getUserWalletId(userWalletRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUserData(
            @RequestHeader ("X-User-Id") UUID userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        UserResponse user = userDataAccessService.updateUserData(userId, updateUserRequest);
        return ResponseEntity.ok(user);
    }
}
