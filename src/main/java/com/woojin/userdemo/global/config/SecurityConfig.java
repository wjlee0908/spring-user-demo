package com.woojin.userdemo.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // 내부적으로 SecurityFilterChain 빈을 생성하여 세부 설정
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 인증되지 않은 모든 페이지의 요청을 허락
        http
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll());

        return http.build();
    }
}
