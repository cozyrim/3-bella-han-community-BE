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
        
        // null이거나 빈 문자열이면 기본 이미지 사용
        if (url == null || url.isBlank()) {
            url = "https://community-image-bucket-1116.s3.ap-northeast-2.amazonaws.com/avatar-default.png";
        }
        // localhost URL이면 기본 이미지로 변환 (배포 환경에서 localhost 접근 불가)
        else if (url.contains("localhost:8080") || url.contains("127.0.0.1:8080")) {
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

