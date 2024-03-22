package com.woojin.userdemo.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "user")
@Getter
@Setter
public class UserConfigProperties {
    private String jwtSecret;
    private String jwtIssuer;
}
