package com.woojin.userdemo.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignUpResponse {
    private Long id;
    private String username;
    private String email;
}
