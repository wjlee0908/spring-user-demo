package com.woojin.userdemo.user;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.util.Objects.isNull;

/**
 * 세션 갱신 필터
 * 요청이 있을 때 마다 세션 ID를 갱신합니다 (새로운 세션 발급)
 */
@Component
@RequiredArgsConstructor
public class SessionRefreshFilter extends OncePerRequestFilter {
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;
    private final SessionUtils sessionUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Session session = sessionUtils.getCurrentOne(request);

        if (isNull(session)) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession newHttpSession = sessionUtils.create(request, session);
        sessionUtils.invalidate(session);

        sessionUtils.addToResponse(newHttpSession, response);
        filterChain.doFilter(request, response);
    }
}
