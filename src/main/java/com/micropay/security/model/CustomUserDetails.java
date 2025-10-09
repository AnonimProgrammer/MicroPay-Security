package com.micropay.security.model;

import com.micropay.security.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record CustomUserDetails(User user, String pinHash) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().getRole().toString()));
    }

    @Override
    public String getPassword() {
        return pinHash;
    }

    @Override
    public String getUsername() {
        return user.getPhoneNumber();
    }

}
