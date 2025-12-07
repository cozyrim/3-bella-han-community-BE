package com.ktbweek4.community.user;


import com.ktbweek4.community.file.S3FileStorage;
import com.ktbweek4.community.user.dto.UserRequestDTO;
import com.ktbweek4.community.user.dto.UserResponseDTO;
import com.ktbweek4.community.user.entity.User;
import com.ktbweek4.community.user.repository.UserRepository;
import com.ktbweek4.community.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private S3FileStorage s3FileStorage;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // @Value로 주입되는 필드들을 리플렉션으로 설정
        ReflectionTestUtils.setField(userService, "uploadDir", "/tmp/uploads");
        ReflectionTestUtils.setField(userService, "defaultAvatarUrl", 
            "https://community-image-bucket-1116.s3.ap-northeast-2.amazonaws.com/avatar-default.png");
    }

    @Test
    @DisplayName("회원 생성 성공 테스트")
    void createUser_Success() {
        // given
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setNickname("testUser");
        userRequestDTO.setPassword("1234");

        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByNickname(userRequestDTO.getNickname())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .userId(1L)
                .email(userRequestDTO.getEmail())
                .nickname(userRequestDTO.getNickname())
                .password("encodedPassword")
                .userProfileUrl("https://community-image-bucket-1116.s3.ap-northeast-2.amazonaws.com/avatar-default.png")
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            // userProfileUrl이 기본 이미지로 설정되었는지 확인
            assertNotNull(user.getUserProfileUrl());
            return savedUser;
        });

        // when
        UserResponseDTO userResponseDTO = userService.create(userRequestDTO);

        // then
        assertNotNull(userResponseDTO);
        assertEquals("test@example.com", userResponseDTO.getEmail());
        assertEquals("testUser", userResponseDTO.getNickname());
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 예외 발생")
    void createUser_DuplicateEmail_ThrowsException() {
        // given
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setNickname("testUser");
        userRequestDTO.setPassword("1234");

        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(true);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.create(userRequestDTO));

        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    @DisplayName("이미 존재하는 닉네임이면 예외 발생")
    void createUser_DuplicateNickname_ThrowsException() {
        // given
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setNickname("testUser");
        userRequestDTO.setPassword("1234");

        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByNickname(userRequestDTO.getNickname())).thenReturn(true);

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.create(userRequestDTO)
        );

        assertEquals("이미 존재하는 닉네임입니다.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}

