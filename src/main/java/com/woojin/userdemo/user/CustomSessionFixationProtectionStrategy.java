package com.woojin.userdemo.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.stereotype.Component;

//@Component
public class CustomSessionFixationProtectionStrategy extends SessionFixationProtectionStrategy {
    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        super.onAuthentication(authentication, request, response);
    }

    @Override
    protected void onSessionChange(String originalSessionId, HttpSession newSession, Authentication auth) {
        super.onSessionChange(originalSessionId, newSession, auth);
    }
}
