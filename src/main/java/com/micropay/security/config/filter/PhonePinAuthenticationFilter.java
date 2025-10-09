package com.micropay.security.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micropay.security.dto.request.AuthRequest;
import com.micropay.security.dto.response.AuthResponse;
import com.micropay.security.model.CustomUserDetails;
import com.micropay.security.service.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class PhonePinAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SECURITY_FORM_PHONE_NUMBER_KEY = "phoneNumber";

    public static final String SECURITY_FORM_PIN_KEY = "pin";

    private static final RequestMatcher PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults()
            .matcher(HttpMethod.POST, "/v1/auth/login");

    private String phoneNumberParameter = SECURITY_FORM_PHONE_NUMBER_KEY;

    private String pinParameter = SECURITY_FORM_PIN_KEY;

    private boolean postOnly = true;

    private final JwtService jwtService;
    private final Validator validator;

    public PhonePinAuthenticationFilter(JwtService jwtService, Validator validator) {
        super(PATH_REQUEST_MATCHER);
        this.jwtService = jwtService;
        this.validator = validator;
    }

    public PhonePinAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService, Validator validator) {
        super(PATH_REQUEST_MATCHER, authenticationManager);
        this.jwtService = jwtService;
        this.validator = validator;
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

            validateRequest(authRequest);

            String phoneNumber = authRequest.phoneNumber();
            phoneNumber = (phoneNumber == null) ? "" : phoneNumber;

            String pin = authRequest.pin();
            pin = (pin == null) ? "" : pin;

            UsernamePasswordAuthenticationToken authToken =
                    UsernamePasswordAuthenticationToken.unauthenticated(phoneNumber, pin);

            setDetails(request, authToken);
            return this.getAuthenticationManager().authenticate(authToken);

        } catch (Exception exception) {
            throw new AuthenticationServiceException("Unable to parse authentication request", exception);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        AuthResponse authResponse = jwtService.generateTokens(userDetails.user());

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    private void validateRequest(AuthRequest authRequest) {
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(authRequest);

        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            throw new AuthenticationServiceException(errorMessage);
        }
    }

}
