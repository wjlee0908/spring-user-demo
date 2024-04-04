package com.woojin.userdemo.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class SessionUtils {
    private static final String SESSION_KEY = "JSESSIONID";

    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    public Session getByRequest(HttpServletRequest request) {
        String sessionId = this.getSessionId(request);

        if(isNull(sessionId)) {
            return null;
        }

        Session session = sessionRepository.findById(sessionId);
        return session;
    }

    private String getSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> sessionCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(SESSION_KEY)).findFirst();

        if(!sessionCookie.isPresent()) {
            return null;
        }

        return sessionCookie.get().getValue();
    }

    public void deleteById(String id) {
        this.sessionRepository.deleteById(id);
    }
}
