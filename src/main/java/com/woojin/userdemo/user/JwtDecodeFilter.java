package com.woojin.userdemo.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @deprecated
 *
 * 로그인이 성공한 사용자에게 받은 JWT를 해석해서 어떤 유저가 서버에 접근했는 지 파악한다
 * 요청 당 한 번만 확인하면 되므로, `OncePerRequestFilter`를 상속 받아 구현
 */
@Deprecated
@Component
@RequiredArgsConstructor
public class JwtDecodeFilter extends OncePerRequestFilter {
    @Value("${user.jwt-secret}")
    private String JWT_SECRET;

    @Value("${user.jwt-issuer}")
    private String JWT_ISSUER;

    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Authorization: Bearer aaa.bbb.ccc
        String header = request.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")) {
            try {
                String accessToken = header.substring(7);

                // JWT 해석
                Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
                JWTVerifier verifier = JWT.require(algorithm).withIssuer(JWT_ISSUER).build();
                DecodedJWT jwt = verifier.verify(accessToken);
                String username = jwt.getSubject();

                User user = (User) userDetailsService.loadUserByUsername(username);
                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (JWTVerificationException exception) {
                exception.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);
    }
}
