package com.ktbweek4.community.user.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserSignupForm {
    private String email;
    private String password;
    private String nickname;
    private MultipartFile profileImage;
}