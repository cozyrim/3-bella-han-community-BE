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
            // S3 기본 아바타 URL 사용
            url = "https://community-image-bucket-1116.s3.ap-northeast-2.amazonaws.com/avatar-default.png";
        }
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userProfileUrl(url)
                .build();
    }
}

