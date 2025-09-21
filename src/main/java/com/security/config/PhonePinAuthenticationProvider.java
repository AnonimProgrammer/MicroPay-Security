package com.security.config;

import com.security.model.CustomUserDetails;
import com.security.service.security.PinManagementService;
import com.security.service.security.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PhonePinAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final UserAuthenticationService userDetailsService;
    private final PinManagementService pinManagementService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        String rawPin = authentication.getCredentials().toString();
        String storedHash = ((CustomUserDetails) userDetails).getPassword();

        pinManagementService.checkPinMatching(rawPin, storedHash);
    }

    @Override
    protected UserDetails retrieveUser(
            String username,
            UsernamePasswordAuthenticationToken authentication
    ) throws AuthenticationException {
        return userDetailsService.loadUserByUsername(username);
    }

}
