package com.ktbweek4.community.user.dto;

import com.ktbweek4.community.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Spring Security 인증용 사용자 정보 클래스
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    // Spring Security에서 로그인한 사용자 정보를 표현하는 클래스
    // 사용자 세부 정보 구현체


    private final User user;

    public Long getUserId() {
        return user.getUserId();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 이메일을 사용자명으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // isDeleted가 null이면 false로 처리 (삭제되지 않음)
        Boolean isDeleted = user.getIsDeleted();
        return isDeleted == null || !isDeleted;
    }
}
