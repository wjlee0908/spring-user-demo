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
 * 요청이 있을 때 마다 요청의 세션 만료 시간을 갱신합니다
 */
@Component
@RequiredArgsConstructor
public class SessionRefreshFilter extends OncePerRequestFilter {
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Session session = (Session) request.getAttribute("session");

        if(isNull(session)) {
            filterChain.doFilter(request, response);
            return;
        }

//        Session newSession = request.getSession(true);

    }
}
