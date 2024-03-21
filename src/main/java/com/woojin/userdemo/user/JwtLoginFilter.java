package com.woojin.userdemo.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    @Value("${USER_JWT_SECRET}")
    private String JWT_SECRET;

    @Value("${USER_JWT_ISSUER}")
    private String JWT_ISSUER;

    /**
     * 로그인 성공 시 호출.
     * 로그인을 성공하면 JWT를 반환한다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        try {
            User user = (User) authResult.getPrincipal();
            String username = user.getUsername();

            // JWT 생성
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            String accessToken = JWT.create()
                    .withIssuer(JWT_ISSUER)
                    .withSubject(username)
                    .sign(algorithm);

            response.getWriter().write(accessToken);
        } catch (JWTCreationException | IOException exception) {
            exception.printStackTrace();
        }
    }
}
