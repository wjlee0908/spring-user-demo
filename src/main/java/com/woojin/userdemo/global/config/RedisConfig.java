package com.woojin.userdemo.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

@Configuration
@EnableRedisIndexedHttpSession(redisNamespace = "${spring.session.redis.namespace}")
public class RedisConfig extends AbstractHttpSessionApplicationInitializer {
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        // JSON serializer
        // 클래스 타입을 패키지 명과 함께 저장하여 타입의 패키지 명이 다르면 조회할 수 없다
        return new GenericJackson2JsonRedisSerializer();
    }
}
