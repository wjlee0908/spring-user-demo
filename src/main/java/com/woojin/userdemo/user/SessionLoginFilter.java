package com.woojin.userdemo.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woojin.userdemo.global.dto.ErrorResponse;
import com.woojin.userdemo.user.dto.UserLoginRequest;
import com.woojin.userdemo.user.dto.UserResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SessionLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final SessionService sessionService;

    public SessionLoginFilter(AuthenticationManager authenticationManager, SessionService sessionService) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.sessionService = sessionService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserLoginRequest userLoginRequest = null;
        try {
            userLoginRequest = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userLoginRequest.getUsername(),
                userLoginRequest.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        try {
            User user = (User) authResult.getPrincipal();

            HttpSession newSession = sessionService.create(request, user.getUsername());
            sessionService.addToResponse(newSession, response);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);

            UserResponse userLoginResponse = new UserResponse(user);
            new ObjectMapper().writeValue(response.getOutputStream(), userLoginResponse);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "INVALID_USERNAME_OR_PASSWORD",
                failed.getMessage()
        );

        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}
