package com.woojin.userdemo.session;

import com.woojin.userdemo.user.User;
import com.woojin.userdemo.user.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.util.Objects.isNull;

/**
 * 세션 인가(Authorization) 필터
 * 요청의 쿠키 - 세션에 있는 사용자 인증 정보를 확인하고 인가되지 않은 사용자면 예외를 throw 합니다.
 */
@Component
@RequiredArgsConstructor
public class SessionAuthorizationFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl userDetailsService;
    private final SessionService sessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Session session = sessionService.findFromRepository(request);

        if (isNull(session)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (session.isExpired()) {
            this.sessionService.invalidate(session);

            filterChain.doFilter(request, response);
            return;
        }

        try {
            sessionService.addToRequest(session, request);

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
