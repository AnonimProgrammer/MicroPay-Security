package com.security.config;

import com.security.config.filter.PhonePinAuthenticationFilter;
import com.security.service.security.UserAuthenticationService;
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
            UserAuthenticationService userAuthenticationService
    ) {
        return new PhonePinAuthenticationFilter(authManager, userAuthenticationService);
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
