package com.woojin.userdemo.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @deprecated Login response for JWT Login
 */
@Getter
@AllArgsConstructor
public class UserLoginResponse {
    private Long id;
    private String accessToken;
}
