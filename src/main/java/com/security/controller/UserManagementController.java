package com.security.controller;

import com.security.model.UserModel;
import com.security.service.UserDataAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserDataAccessService userDataAccessService;

    @GetMapping
    public ResponseEntity<UserModel> getUserData(@RequestHeader ("X-User-Id") UUID userId) {
        UserModel user = userDataAccessService.getUserData(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public void updateUserData(
            @RequestHeader ("X-User-Id") UUID userId,
            @Valid @RequestBody UserModel user
    ) {
        userDataAccessService.updateUserData(userId, user);
    }
}
