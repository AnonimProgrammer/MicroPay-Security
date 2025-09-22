package com.security.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserModel {

    private String phoneNumber;
    private String fullName;
    private String email;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserModel(Builder builder) {
        this.phoneNumber = builder.phoneNumber;
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public static class Builder {
        private String phoneNumber;
        private String fullName;
        private String email;
        private UserStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }
        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        public UserModel build() {
            return new UserModel(this);
        }

    }
}
