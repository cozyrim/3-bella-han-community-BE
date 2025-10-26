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
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userProfileUrl(user.getUserProfileUrl())
                .build();
    }
}

