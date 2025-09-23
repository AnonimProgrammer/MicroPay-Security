package com.micropay.security.repo;

import com.micropay.security.model.RoleType;
import com.micropay.security.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRole(RoleType roleType);
}
