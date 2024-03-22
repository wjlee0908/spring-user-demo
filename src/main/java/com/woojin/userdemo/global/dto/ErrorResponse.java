package com.woojin.userdemo.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private List<ApiError> errors;

    public ErrorResponse(int status, String code, String message) {
        this.status = status;
        this.errors = Arrays.asList(new ApiError(code, message));
    }

    public ErrorResponse(HttpStatusCode statusCode, String code, String message) {
        this.status = statusCode.value();
        this.errors = Arrays.asList(new ApiError(code, message));
    }
}
