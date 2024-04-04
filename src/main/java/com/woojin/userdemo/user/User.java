package com.woojin.userdemo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@Setter
@Table
public class User implements UserDetails {
    public static final Integer MIN_USERNAME_LENGTH = 3;
    public static final Integer MAX_USERNAME_LENGTH = 25;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    @JsonIgnore
    private String password;

    @Column(unique = true)
    private String email;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorityCollection = new ArrayList<GrantedAuthority>();
        authorityCollection.add(() -> {
            return "ROLE_USER";
        });

        return authorityCollection;
    }

    /**
     * 유효한 계정 여부
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 잠금(사용불가 처리)하지 않은 계정 여부
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
