package com.security.service;

import com.security.model.CustomUserDetails;
import com.security.model.entity.Credential;
import com.security.model.entity.User;
import com.security.repo.CredentialRepository;
import com.security.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        System.out.println("Phone number: " + phoneNumber);

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found."));
        Credential credential = credentialRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Credentials not found."));

        return new CustomUserDetails(user, credential.getPinHash());
    }
}
