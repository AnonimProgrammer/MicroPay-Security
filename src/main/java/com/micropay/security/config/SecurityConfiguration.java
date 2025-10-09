package com.micropay.security.config;

import com.micropay.security.config.filter.PhonePinAuthenticationFilter;
import com.micropay.security.config.provider.PhonePinAuthenticationProvider;
import com.micropay.security.service.security.JwtService;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PhonePinAuthenticationFilter phonePinAuthenticationFilter(
            AuthenticationManager authManager,
            JwtService jwtService,
            Validator validator
    ) {
        return new PhonePinAuthenticationFilter(authManager, jwtService, validator);
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http, PhonePinAuthenticationFilter phonePinAuthenticationFilter
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .anyRequest().permitAll()
                )
                .addFilterAt(phonePinAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            PhonePinAuthenticationProvider provider
    ) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
