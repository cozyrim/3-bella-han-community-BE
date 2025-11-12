package com.ktbweek4.community.user.service;

import com.ktbweek4.community.file.S3FileStorage;
import com.ktbweek4.community.user.dto.UserRequestDTO;
import com.ktbweek4.community.user.dto.UserResponseDTO;
import com.ktbweek4.community.user.entity.User;
import com.ktbweek4.community.user.repository.UserRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3FileStorage fileStorage;

    @Value("${aws.s3.default-avatar-url}")
    private String defaultAvatarUrl;

    public UserResponseDTO create(UserRequestDTO request) {
        validateCreate(request);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        String profileUrl = defaultAvatarUrl;

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(encodedPassword)
                .userProfileUrl(profileUrl)
                .build();

        User saved = userRepository.save(user);

        return  UserResponseDTO.of(saved);
    }

    // 프로필 사진과 함께 회원가입
    public UserResponseDTO createWithProfile(UserRequestDTO request, MultipartFile profileImage) {
        validateCreate(request);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        String profileUrl = defaultAvatarUrl;

        // 프로필 사진이 있는 경우 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                var stored = fileStorage.save(profileImage, "profile");
                profileUrl = stored.publicUrl();

            } catch (IOException e) {
                throw new RuntimeException("프로필 사진 저장 중 오류가 발생했습니다.", e);
            }
        }


        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(encodedPassword)
                .userProfileUrl(profileUrl)
                .build();

        User saved = userRepository.save(user);
        return UserResponseDTO.of(saved);
    }

    private void validateCreate(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
    }

    public User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    public User findByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
    
    // 이메일 중복 검사
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // 닉네임 중복 검사
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    

}
