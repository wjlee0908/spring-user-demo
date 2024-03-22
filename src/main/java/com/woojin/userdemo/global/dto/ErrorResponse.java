package com.woojin.userdemo.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private List<ApiError> errors;
}
