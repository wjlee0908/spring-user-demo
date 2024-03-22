package com.woojin.userdemo.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woojin.userdemo.global.dto.ApiError;
import com.woojin.userdemo.global.dto.ErrorResponse;
import com.woojin.userdemo.user.dto.UserLoginRequest;
import com.woojin.userdemo.user.dto.UserLoginResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Arrays;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final UserConfigProperties userConfigProperties;

    private final AuthenticationManager authenticationManager;

    public JwtLoginFilter(AuthenticationManager authenticationManager, UserConfigProperties userConfigProperties) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.userConfigProperties = userConfigProperties;
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

    /**
     * 로그인 성공 시 호출.
     * 로그인을 성공하면 JWT를 반환한다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        try {
            User user = (User) authResult.getPrincipal();

            String jwtSecret = userConfigProperties.getJwtSecret();
            String jwtIssuer = userConfigProperties.getJwtIssuer();

            // JWT 생성
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            String accessToken = JWT.create()
                    .withIssuer(jwtIssuer)
                    .withSubject(user.getUsername())
                    .sign(algorithm);

            UserLoginResponse userLoginResponse = new UserLoginResponse(user.getId(), accessToken);
            new ObjectMapper().writeValue(response.getOutputStream(), userLoginResponse);
        } catch (JWTCreationException | IOException exception) {
            exception.printStackTrace();
        }
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
