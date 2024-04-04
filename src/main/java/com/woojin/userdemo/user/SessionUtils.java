package com.woojin.userdemo.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class SessionUtils {
    public static String SESSION_KEY = "JSESSIONID";

    /**
     * 새로운 세션을 생성합니다
     */
    public HttpSession create(HttpServletRequest request, String username) {
        HttpSession session = request.getSession(false);
        if(!isNull(session)) {
            session.invalidate();
        }

        HttpSession newSession = request.getSession(true);
        newSession.setMaxInactiveInterval(60 * 60); // 세션 유효 시간
        newSession.setAttribute("username", username); // 인증된 사용자 정보를 세션에 저장

        return newSession;
    }

    /**
     * 세션을 response cookie에 추가합니다
     */
    public void addToResponse(HttpSession session, HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_KEY, session.getId());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
