package com.woojin.userdemo.user;

import com.woojin.userdemo.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserDetailsService - 사용자 조회 기능
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;


    /**
     * username으로 사용자 조회.
     * 비밀번호 일치 여부 확인 기능 내장
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> _user = this.userRepository.findByUsername(username);

        if(_user.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }

        User user = _user.get();
        List<GrantedAuthority> authorities = new ArrayList<>();

        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority((UserRole.USER.getValue())));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
