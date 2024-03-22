package com.woojin.userdemo.user;

import com.woojin.userdemo.global.dto.ApiError;
import com.woojin.userdemo.global.dto.ErrorResponse;
import com.woojin.userdemo.user.dto.UserSignUpRequest;
import com.woojin.userdemo.user.dto.UserSignUpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid UserSignUpRequest userSignUpRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    bindingResult.getAllErrors().stream().map(
                            error -> new ApiError(error.getCode(), error.getDefaultMessage())
                    ).collect(Collectors.toList())
            ));
        }

        if(!userSignUpRequest.getPassword().equals(userSignUpRequest.getPasswordConfirm())) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "PASSWORD_INCORRECT", "Password confirmation does not match"));
        }

        try {
            User user = userService.create(userSignUpRequest.getUsername(), userSignUpRequest.getEmail(), userSignUpRequest.getPassword());
            UserSignUpResponse userSignUpResponse = new UserSignUpResponse(
                user.getId(), user.getUsername(), user.getEmail()
            );
            return ResponseEntity.ok(userSignUpResponse);
        } catch(DataIntegrityViolationException err) {
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse(HttpStatus.CONFLICT.value(), "USER_ALREADY_EXISTS", "User already exists")
            );
        } catch (Exception err) {
            err.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SIGN_UP_FAILED", err.getMessage())
            );
        }
    }
}
