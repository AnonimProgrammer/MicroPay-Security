package com.micropay.security.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserModel {

    private UUID id;
    private String phoneNumber;
    private String fullName;
    private String email;
    private RoleType role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
