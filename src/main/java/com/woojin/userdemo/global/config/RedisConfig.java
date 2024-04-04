package com.woojin.userdemo.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

@Configuration
@EnableRedisIndexedHttpSession(redisNamespace = "${spring.session.redis.namespace}")
public class RedisConfig extends AbstractHttpSessionApplicationInitializer {
}
