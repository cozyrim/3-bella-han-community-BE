package com.ktbweek4.community.user.dto;

import com.ktbweek4.community.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long userId;
    private String email;
    private String nickname;
    private String userProfileUrl;

    public static UserResponseDTO of(User user) {
        String url = user.getUserProfileUrl();
        if (url == null || url.isBlank()) {
            url = "http://localhost:8080/files/avatar-default.png";
        }
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userProfileUrl(url)
                .build();
    }
}

