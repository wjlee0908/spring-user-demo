package com.woojin.userdemo.user;

import com.woojin.userdemo.user.exceptions.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * 세션 인가(Authorization) 필터
 * 요청의 쿠키 - 세션에 있는 사용자 인증 정보를 확인하고 인가되지 않은 사용자면 예외를 throw 합니다.
 */
@Component
@RequiredArgsConstructor
public class SessionAuthorizationFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl userDetailsService;
    private final SessionUtils sessionUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Session session = sessionUtils.getByRequest(request);

        if(isNull(session)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = session.getAttribute("username");
            User user = (User) userDetailsService.loadUserByUsername(username);

            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}
