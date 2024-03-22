package com.woojin.userdemo.user.dto;

import com.woojin.userdemo.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserGetResponse {
    private Long id;
    private String username;
    private String email;

    public UserGetResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
