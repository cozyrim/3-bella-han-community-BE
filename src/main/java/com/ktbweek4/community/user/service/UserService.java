package com.ktbweek4.community.user.service;

import com.ktbweek4.community.file.S3FileStorage;
import com.ktbweek4.community.user.dto.PasswordUpdateDTO;
import com.ktbweek4.community.user.dto.UserRequestDTO;
import com.ktbweek4.community.user.dto.UserResponseDTO;
import com.ktbweek4.community.user.dto.UserUpdateDTO;
import com.ktbweek4.community.user.entity.User;
import com.ktbweek4.community.user.repository.UserRepository;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3FileStorage s3FileStorage;
    
    @Value("${app.upload.dir}")
    private String uploadDir;
    
    @Value("${app.default.avatar-url:https://community-image-bucket-1116.s3.ap-northeast-2.amazonaws.com/avatar-default.png}")
    private String defaultAvatarUrl;

    public UserResponseDTO create(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 프로필 이미지 URL 처리: 전달된 URL이 있으면 사용, 없으면 기본 이미지
        String profileUrl = (request.getProfileImageUrl() != null && !request.getProfileImageUrl().trim().isEmpty())
                ? request.getProfileImageUrl()
                : defaultAvatarUrl;

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(encodedPassword)
                .userProfileUrl(profileUrl)
                .build();

        User saved = userRepository.save(user);

        return  UserResponseDTO.of(saved);
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
    
    // 프로필 사진과 함께 회원가입
    public UserResponseDTO createWithProfile(UserRequestDTO request, MultipartFile profileImage) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        String profileUrl = null;
        
        // 프로필 사진이 있는 경우 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                profileUrl = saveProfileImage(profileImage);
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
    
    // 프로필 사진 저장
    private String saveProfileImage(MultipartFile profileImage) throws IOException {
        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 파일명 생성 (UUID + 원본 확장자)
        String originalFilename = profileImage.getOriginalFilename();
        String extension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID().toString() + extension;
        
        // 파일 저장
        Path filePath = uploadPath.resolve(filename);
        Files.copy(profileImage.getInputStream(), filePath);
        
        // URL 반환 (WebMvcConfig에서 설정한 경로 사용)
        return "/files/" + filename;
    }
    
    /**
     * 프로필 업데이트 (닉네임, 프로필 이미지)
     * - 이메일은 변경 불가
     */
    public UserResponseDTO updateProfile(Long userId, UserUpdateDTO updateDTO) {
        User user = findByIdOrThrow(userId);
        
        // 닉네임 변경 (변경할 닉네임이 있고, 기존 닉네임과 다른 경우)
        if (updateDTO.getNickname() != null && !updateDTO.getNickname().isBlank()) {
            if (!user.getNickname().equals(updateDTO.getNickname())) {
                // 닉네임 중복 검사
                if (userRepository.existsByNickname(updateDTO.getNickname())) {
                    throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
                }
                user.setNickname(updateDTO.getNickname());
            }
        }
        
        // 프로필 이미지 변경 (변경할 URL이 있는 경우)
        if (updateDTO.getProfileImageUrl() != null && !updateDTO.getProfileImageUrl().isBlank()) {
            user.setUserProfileUrl(updateDTO.getProfileImageUrl());
        }
        
        User updated = userRepository.save(user);
        return UserResponseDTO.of(updated);
    }
    
    /**
     * 비밀번호 변경
     */
    public void updatePassword(Long userId, PasswordUpdateDTO passwordDTO) {
        User user = findByIdOrThrow(userId);
        
        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        
        // 새 비밀번호 암호화 및 저장
        String encodedNewPassword = passwordEncoder.encode(passwordDTO.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }
}
