package com.woojin.userdemo.session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class SessionService {
    private static final String REQUEST_ATTRIBUTE_KEY = "session";
    public static String COOKIE_KEY = "JSESSIONID";
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    /**
     * 새로운 세션을 생성합니다
     */
    public HttpSession create(HttpServletRequest request, String username) {
        HttpSession session = request.getSession(false);
        if (!isNull(session)) {
            session.invalidate();
        }

        HttpSession newSession = request.getSession(true);
        newSession.setMaxInactiveInterval(60 * 60); // 세션 유효 시간
        newSession.setAttribute("username", username); // 인증된 사용자 정보를 세션에 저장

        return newSession;
    }

    /**
     * 주어진 session과 같은 정보를 가진 session을 생성합니다
     */
    public HttpSession create(HttpServletRequest request, Session session) {
        String username = session.getAttribute("username");
        return this.create(request, username);
    }

    /**
     * request cookie의 SESSION_KEY를 이용해서 repository에서 session을 fetch합니다.
     */
    public Session findFromRepository(HttpServletRequest request) {
        String sessionId = this.findIdFromCookie(request);

        if (isNull(sessionId)) {
            return null;
        }

        Session session = sessionRepository.findById(sessionId);
        return session;
    }

    public Session findFromRepository(HttpSession httpSession) {
        String sessionId = httpSession.getId();

        if (isNull(sessionId)) {
            return null;
        }

        Session session = sessionRepository.findById(sessionId);
        return session;
    }

    /**
     * request의 cookie에서 session id를 찾습니다
     */
    private String findIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> sessionCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(SessionService.COOKIE_KEY)).findFirst();

        if (!sessionCookie.isPresent()) {
            return null;
        }

        return sessionCookie.get().getValue();
    }

    /**
     * 이전 필터에서 request에 저장했던 세션을 불러옵니다
     */
    public Session getCurrentOne(HttpServletRequest request) {
        return (Session) request.getAttribute(REQUEST_ATTRIBUTE_KEY);
    }

    /**
     * 세션을 request에 추가하여 다음 필터에서 접근할 수 있도록 합니다
     */
    public void addToRequest(Session session, HttpServletRequest request) {
        request.setAttribute(REQUEST_ATTRIBUTE_KEY, session);
    }

    /**
     * 세션을 response cookie에 추가합니다
     */
    public void addToResponse(HttpSession session, HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_KEY, session.getId());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void invalidate(Session session) {
        this.sessionRepository.deleteById(session.getId());
    }
}
