package com.woojin.userdemo.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class LogoutHandlerImpl implements LogoutHandler, LogoutSuccessHandler {
    private final SessionService sessionService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Session session = sessionService.getCurrentOne(request);
        if (!isNull(session)) {
            sessionService.invalidate(session);
        }

        Cookie sessionCookie = new Cookie(SessionService.COOKIE_KEY, null);
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Logout successful");
    }
}
