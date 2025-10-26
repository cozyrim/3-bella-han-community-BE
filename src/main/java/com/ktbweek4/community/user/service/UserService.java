package com.ktbweek4.community.user.service;

import com.ktbweek4.community.user.dto.UserRequestDTO;
import com.ktbweek4.community.user.dto.UserResponseDTO;
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
    
    @Value("${app.upload.dir}")
    private String uploadDir;

    public UserResponseDTO create(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(encodedPassword)
                .userProfileUrl(null)
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
}
