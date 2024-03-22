package com.woojin.userdemo.global.config;

import com.woojin.userdemo.user.JwtDecodeFilter;
import com.woojin.userdemo.user.JwtLoginFilter;
import com.woojin.userdemo.user.UserConfigProperties;
import com.woojin.userdemo.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] ALLOW_URLS = {
            "/users/login",
            "/users/signup",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    private final JwtDecodeFilter jwtDecodeFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserConfigProperties userConfigProperties;

    @Bean
    // 내부적으로 SecurityFilterChain 빈을 생성하여 세부 설정
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager, userConfigProperties);
        jwtLoginFilter.setUsernameParameter("username");
        jwtLoginFilter.setPasswordParameter("password");
        jwtLoginFilter.setFilterProcessesUrl("/users/login");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeRequests()
                .requestMatchers(ALLOW_URLS).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationManager(authenticationManager)
                .addFilterBefore(jwtDecodeFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)
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
}
