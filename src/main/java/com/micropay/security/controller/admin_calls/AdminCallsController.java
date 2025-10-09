package com.micropay.security.controller.admin_calls;

import com.micropay.security.dto.response.CursorPage;
import com.micropay.security.model.UserModel;
import com.micropay.security.model.UserStatus;
import com.micropay.security.service.user.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
public class AdminCallsController {

    private final UserManagementService userManagementService;

    @PatchMapping("/block-user")
    public ResponseEntity<Object> blockUser(@RequestHeader ("X-User-Id") UUID userId) {
        userManagementService.blockUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/suspend-user")
    public ResponseEntity<Object> suspendUser(@RequestHeader ("X-User-Id") UUID userId) {
        userManagementService.suspendUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/activate-user")
    public ResponseEntity<Object> activateUser(@RequestHeader ("X-User-Id") UUID userId) {
        userManagementService.activateUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<CursorPage<UserModel>> getUsers(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorDate,
            @RequestParam(required = false) String sortBy
    ) {
        CursorPage<UserModel> result = userManagementService.getUsers(status, pageSize, cursorDate, sortBy);
        return ResponseEntity.ok(result);
    }

}
