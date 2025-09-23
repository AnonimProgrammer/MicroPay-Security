package com.micropay.security.controller.internal;

import com.micropay.security.model.UserStatus;
import com.micropay.security.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalCallsController {

    private final UserManagementService userManagementService;

    @PutMapping("/block-user")
    public void blockUser(@RequestHeader ("X-User-Id") UUID userId) {
        userManagementService
                .updateUserStatus(userId, UserStatus.BLOCKED);
    }

    @PutMapping("/suspend-user")
    public void suspendUser(@RequestHeader ("X-User-Id") UUID userId) {
        userManagementService
                .updateUserStatus(userId, UserStatus.SUSPENDED);
    }

    @PutMapping("/activate-user")
    public void activateUser(@RequestHeader ("X-User-Id") UUID userId) {
        userManagementService
                .updateUserStatus(userId, UserStatus.ACTIVE);
    }

}
