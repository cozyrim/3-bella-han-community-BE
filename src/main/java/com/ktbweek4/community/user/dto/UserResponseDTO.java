package com.ktbweek4.community.user.dto;

import com.ktbweek4.community.user.entity.User;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long userId;
    private String email;
    private String nickname;

    public UserResponseDTO(Long id, String email, String nickname) {
        this.userId = id;
        this.email = email;
        this.nickname = nickname;
    }

    public static UserResponseDTO of(User user) {
        return new UserResponseDTO(user.getUserId(), user.getEmail(), user.getNickname());
    }

}

