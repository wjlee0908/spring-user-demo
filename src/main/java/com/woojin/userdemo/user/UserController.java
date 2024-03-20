package com.woojin.userdemo.user;

import com.woojin.userdemo.user.dto.UserCreateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

    @GetMapping("/signup")
    public String signup(UserCreateDto userCreateDto) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateDto userCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if(!userCreateDto.getPassword().equals(userCreateDto.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "PASSWORD_INCORRECT", "패스워드 확인이 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword());
        } catch(DataIntegrityViolationException err) {
            err.printStackTrace();
            bindingResult.reject("SIGN_UP_FAILED", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception err) {
            err.printStackTrace();
            bindingResult.reject("SIGN_UP_FAILED", err.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }
}
