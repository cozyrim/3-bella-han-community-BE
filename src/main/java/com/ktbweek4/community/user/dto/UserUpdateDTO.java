package com.ktbweek4.community.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 프로필 업데이트 DTO
 * - 닉네임, 프로필 이미지만 변경 가능
 * - 이메일과 비밀번호는 별도 엔드포인트로 변경
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    private String nickname;
    private String profileImageUrl;
}

