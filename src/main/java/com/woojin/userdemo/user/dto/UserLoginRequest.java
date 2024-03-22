package com.woojin.userdemo.user.dto;

import lombok.Getter;

@Getter
public class UserLoginRequest {
    private String username;
    private String password;
}
