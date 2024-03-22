package com.woojin.userdemo.user;

import com.woojin.userdemo.global.dto.ApiError;
import com.woojin.userdemo.global.dto.ErrorResponse;
import com.woojin.userdemo.user.dto.UserResponse;
import com.woojin.userdemo.user.dto.UserSignUpRequest;
import com.woojin.userdemo.user.dto.UserSignUpResponse;
import com.woojin.userdemo.user.dto.UserUpdateRequest;
import com.woojin.userdemo.user.exceptions.UnauthorizedException;
import com.woojin.userdemo.user.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "User CRUD 및 회원가입, 로그인")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    @Operation(summary = "id가 userId인 유저 한 명을 조회합니다")
    @Parameter(name = "userId", description = "조회할 유저의 id")
    public ResponseEntity getById(@PathVariable Long userId) {
        try {
            User user = userService.getById(userId);

            return ResponseEntity.ok(new UserResponse(user));
        } catch (UserNotFoundException err) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", err.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity getMine(Authentication authentication) {
        try {
            User user = userService.getByAuthentication(authentication);

            return ResponseEntity.ok(new UserResponse(user));
        } catch (UnauthorizedException unauthorizedException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Cannot read current authentication"));
        } catch (UserNotFoundException userNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "Cannot found user with current authentication"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid UserSignUpRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    bindingResult.getAllErrors().stream().map(
                            error -> new ApiError(error.getCode(), error.getDefaultMessage())
                    ).collect(Collectors.toList())
            ));
        }

        if(!userService.isPasswordMatchingConfirmation(request.getPassword(), request.getPasswordConfirm())) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "PASSWORD_INCORRECT", "Password confirmation does not match"));
        }

        try {
            User user = userService.create(request.getUsername(), request.getEmail(), request.getPassword());
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

    @PatchMapping("/me")
    public ResponseEntity updateMine(Authentication authentication, UserUpdateRequest request) {
        try {
            User user = userService.getByAuthentication(authentication);

            if(request.getPassword() != null
               && !userService.isPasswordMatchingConfirmation(request.getPassword(), request.getPasswordConfirm())) {
                return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "PASSWORD_INCORRECT", "Password confirmation does not match"));
            }

            User updatedUser = userService.update(user.getId(), request.getEmail(), request.getPassword());

            return ResponseEntity.ok(new UserResponse(updatedUser));
        } catch (Exception err) {
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "UPDATE_FAILED", err.getMessage())
            );
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity deleteMine(Authentication authentication) {
        try {
            User user = userService.getByAuthentication(authentication);
            userService.delete(user.getId());

            return ResponseEntity.ok(new UserResponse(user));
        } catch (Exception err) {
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "UPDATE_FAILED", err.getMessage())
            );
        }
    }
}
