package com.security.model.entity;

import com.security.model.UserStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String fullName;

    private String email;

    @ManyToOne(optional = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public User(Builder builder) {
        this.phoneNumber = builder.phoneNumber;
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.role = builder.role;
        this.status = builder.status;
    }

    public User() {}

    public static class Builder {
        private String phoneNumber;
        private String fullName;
        private String email;
        private Role role;
        private UserStatus status;

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
        public Builder role(Role role) {
            this.role = role;
            return this;
        }
        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }
        public User build() {
            return new User(this);
        }

    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
