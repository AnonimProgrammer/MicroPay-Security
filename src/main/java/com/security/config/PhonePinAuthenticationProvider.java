package com.security.config;

import com.security.model.CustomUserDetails;
import com.security.service.CustomUserDetailsService;
import com.security.service.PinService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PhonePinAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PinService pinService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        String rawPin = authentication.getCredentials().toString();
        String storedHash = ((CustomUserDetails) userDetails).getPassword();

        System.out.println("Raw PIN: " + rawPin);

        if (!pinService.verifyPin(rawPin, storedHash)) {
            throw new BadCredentialsException("Invalid PIN");
        }
    }

    @Override
    protected UserDetails retrieveUser(
            String username,
            UsernamePasswordAuthenticationToken authentication
    ) throws AuthenticationException {
        return userDetailsService.loadUserByUsername(username);
    }

}
