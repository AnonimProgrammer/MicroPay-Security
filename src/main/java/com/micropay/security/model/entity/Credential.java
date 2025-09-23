package com.micropay.security.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "credentials")
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String pinHash;

    private boolean biometricEnabled;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Credential() {
    }
    public Credential(Builder builder) {
        this.user = builder.user;
        this.pinHash = builder.pinHash;
        this.biometricEnabled = builder.biometricEnabled;
    }

    public static class Builder {
        private User user;
        private String pinHash;
        private boolean biometricEnabled;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder pinHash(String pinHash) {
            this.pinHash = pinHash;
            return this;
        }

        public Builder withBiometricEnabled(boolean biometricEnabled) {
            this.biometricEnabled = biometricEnabled;
            return this;
        }

        public Credential build() {
            return new Credential(this);
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
