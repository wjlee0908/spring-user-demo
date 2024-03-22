package com.woojin.userdemo.user.dto;

import com.woojin.userdemo.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "사용자 응답 DTO")
public class UserResponse {
    @Schema(description = "사용자의 DB id", example = "1")
    private Long id;

    @Schema(description = "사용자가 로그인할 때 입력하는 아이디", example = "tester")
    private String username;

    @Schema(description = "사용자의 이메일 주소", example = "tester@test.com")
    private String email;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
