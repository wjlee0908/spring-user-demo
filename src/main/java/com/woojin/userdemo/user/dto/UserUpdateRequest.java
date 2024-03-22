package com.woojin.userdemo.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String password;

    private String passwordConfirm;

    @Email
    private String email;
}
