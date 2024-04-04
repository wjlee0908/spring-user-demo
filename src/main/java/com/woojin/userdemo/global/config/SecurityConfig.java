package com.woojin.userdemo.global.config;

import com.woojin.userdemo.user.SessionAuthorizationFilter;
import com.woojin.userdemo.user.SessionLoginFilter;
import com.woojin.userdemo.user.SessionUtils;
import com.woojin.userdemo.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] ALLOW_URLS = {
            "/users/login",
            "/users/signup",
            "/error",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    private final UserDetailsServiceImpl userDetailsService;
    private final RedisIndexedSessionRepository redisIndexedSessionRepository;
    private final SessionAuthorizationFilter sessionAuthorizationFilter;
    private final SessionUtils sessionUtils;

    @Bean
    // 내부적으로 SecurityFilterChain 빈을 생성하여 세부 설정
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        SessionLoginFilter sessionLoginFilter = new SessionLoginFilter(authenticationManager, sessionUtils);
        sessionLoginFilter.setUsernameParameter("username");
        sessionLoginFilter.setPasswordParameter("password");
        sessionLoginFilter.setFilterProcessesUrl("/users/login");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeRequests()
                    .requestMatchers(ALLOW_URLS).permitAll()
                    .anyRequest().authenticated()
                .and()
                .httpBasic(AbstractHttpConfigurer::disable)
                .authenticationManager(authenticationManager)
                .addFilterBefore(sessionAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(sessionLoginFilter, UsernamePasswordAuthenticationFilter.class)
//                .sessionManagement(sessionManagement -> sessionManagement
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                        .sessionFixation(sessionFixation -> sessionFixation.newSession())
//                        .maximumSessions(100)
//                        .sessionConcurrency(sessionConcurrency -> sessionConcurrency.sessionRegistry(sessionRegistry()))
//                )
                .build();
    }

    /**
     * 비밀번호 암호화. Hash
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security의 인증을 처리하는 Bean
     * 사용자 인증 시 `UserSecurityService`와 `PasswordEncoder`를 내부적으로 사용하여 인증과 권한 프로세스 처리
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(this.redisIndexedSessionRepository);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

}
