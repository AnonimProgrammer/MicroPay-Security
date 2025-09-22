package com.security.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.dto.request.AuthRequest;
import com.security.dto.response.AuthResponse;
import com.security.model.CustomUserDetails;
import com.security.service.security.UserAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

@Getter
@Setter
public class PhonePinAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SECURITY_FORM_PHONE_NUMBER_KEY = "phoneNumber";

    public static final String SECURITY_FORM_PIN_KEY = "pin";

    private static final RequestMatcher PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults()
            .matcher(HttpMethod.POST, "/auth/login");

    private String phoneNumberParameter = SECURITY_FORM_PHONE_NUMBER_KEY;

    private String pinParameter = SECURITY_FORM_PIN_KEY;

    private boolean postOnly = true;

    private final UserAuthenticationService userAuthenticationService;

    public PhonePinAuthenticationFilter(UserAuthenticationService userAuthenticationService) {
        super(PATH_REQUEST_MATCHER);
        this.userAuthenticationService = userAuthenticationService;
    }

    public PhonePinAuthenticationFilter(AuthenticationManager authenticationManager, UserAuthenticationService userAuthenticationService) {
        super(PATH_REQUEST_MATCHER, authenticationManager);
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {

        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            AuthRequest authRequest = mapper.readValue(request.getInputStream(), AuthRequest.class);

            String phoneNumber = authRequest.getPhoneNumber();
            phoneNumber = (phoneNumber == null) ? "" : phoneNumber;

            String pin = authRequest.getPin();
            pin = (pin == null) ? "" : pin;

            UsernamePasswordAuthenticationToken authToken =
                    UsernamePasswordAuthenticationToken.unauthenticated(phoneNumber, pin);

            setDetails(request, authToken);
            return this.getAuthenticationManager().authenticate(authToken);

        } catch (Exception e) {
            throw new AuthenticationServiceException("Unable to parse authentication request", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        AuthResponse authResponse = userAuthenticationService.generateTokens(userDetails.user());

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

}
