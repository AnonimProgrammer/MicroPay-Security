package com.security.model.entity;

import jakarta.persistence.*;
import lombok.Data;

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

    @Column(nullable = false)
    private String salt;

    private boolean biometricEnabled;
}
