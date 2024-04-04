package com.woojin.userdemo.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 세션 갱신 필터
 * 요청이 있을 때 마다 요청의 세션 만료 시간을 갱신합니다
 */
@Component
@RequiredArgsConstructor
public class SessionRefreshFilter extends OncePerRequestFilter {
}
