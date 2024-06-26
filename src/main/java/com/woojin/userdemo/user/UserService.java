package com.woojin.userdemo.user;

import com.woojin.userdemo.user.exceptions.UnauthorizedException;
import com.woojin.userdemo.user.exceptions.UserNotFoundException;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }
    }

    public User getByAuthentication(Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedException("User authentication is null");
        }

        User user = (User) authentication.getPrincipal();

        if (user == null) {
            throw new UserNotFoundException("Cannot found user with current authentication");
        }

        return user;
    }

    public User create(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);

        return user;
    }

    public Boolean isPasswordMatchingConfirmation(String password, String passwordConfirm) {
        return password.equals(passwordConfirm);
    }

    public User update(Long id, @Email String email, String password) {
        User user = this.getById(id);

        if (email != null) {
            user.setEmail(email);
        }

        if (password != null) {
            user.setPassword(passwordEncoder.encode(password));
        }

        return userRepository.save(user);
    }

    public void delete(Long id) {
        User user = this.getById(id);
        userRepository.delete(user);
    }
}
