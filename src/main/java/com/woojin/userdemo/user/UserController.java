package com.woojin.userdemo.user;

import com.woojin.userdemo.user.dto.UserCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid UserCreateRequest userCreateRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        if(!userCreateRequest.getPassword().equals(userCreateRequest.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "PASSWORD_INCORRECT", "Password confirmation does not match");
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            userService.create(userCreateRequest.getUsername(), userCreateRequest.getEmail(), userCreateRequest.getPassword());
        } catch(DataIntegrityViolationException err) {
            err.printStackTrace();
            bindingResult.reject("SIGN_UP_FAILED", "User already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(bindingResult.getAllErrors());
        } catch (Exception err) {
            err.printStackTrace();
            bindingResult.reject("SIGN_UP_FAILED", err.getMessage());
            return ResponseEntity.internalServerError().body(bindingResult.getAllErrors());
        }

        return new ResponseEntity("OK", HttpStatus.CREATED);
    }
}
