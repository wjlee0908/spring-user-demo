package com.woojin.userdemo.user;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.util.Objects.isNull;

/**
 * 세션 인가(Authorization) 필터
 * 요청의 쿠키 - 세션에 있는 사용자 인증 정보를 확인하고 인가되지 않은 사용자면 예외를 throw 합니다.
 */
@Component
public class SessionAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Cookie[] cookies = request.getCookies();
        SecurityContext context = SecurityContextHolder.getContext();

        if(!isNull(session)) {
            Object principal = SecurityContextHolder.getContext().getAuthentication();

            String sessionId = session.getId();

            User user = (User) session.getAttribute("user");
        }

        filterChain.doFilter(request, response);
    }
}
