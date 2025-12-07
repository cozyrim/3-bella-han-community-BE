package com.ktbweek4.community.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 변경 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordUpdateDTO {
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;
    
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    private String newPassword;
}

