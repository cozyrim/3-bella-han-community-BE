# 따끈따끈 식탁🍽️ - 백엔드

음식 커뮤니티 플랫폼의 백엔드 서버입니다.

### 커뮤니티 바로가기
https://community-a-feast-of.n-e.kr/

### 시연 영상
https://drive.google.com/file/d/1HH1I-RAZ8O5SP3cmMB1hxNsOlXyn7bTF/view?usp=sharing

## 아키텍처 구조
<img width="1197" height="681" alt="Frame 1" src="https://github.com/user-attachments/assets/c8a969fb-97fb-4de4-930c-fa318656cc16" />


## 📋 목차

- [기술 스택]
- [프로젝트 구조]
- [주요 기능]


## 🛠 기술 스택

- **Java 21** - 프로그래밍 언어
- **Spring Boot 3.5.6** - 프레임워크
- **Spring Security** - 인증/인가
- **Spring Data JPA** - 데이터베이스 ORM
- **MySQL** - 관계형 데이터베이스
- **JWT** - 토큰 기반 인증
- **AWS S3** - 파일 스토리지
- **QueryDSL** - 동적 쿼리 작성
- **Swagger/OpenAPI** - API 문서화

## 📁 프로젝트 구조

```
src/main/java/com/ktbweek4/community/
├── auth/                    # 인증 관련
│   ├── AuthController.java  # 로그인/로그아웃
│   ├── jwt/                 # JWT 토큰 처리
│   └── entity/              # RefreshToken 엔티티
│
├── user/                    # 사용자 관리
│   ├── controller/          # UserController
│   ├── service/             # UserService, CustomUserDetailsService
│   ├── repository/          # UserRepository
│   ├── entity/              # User, UsersLike
│   └── dto/                 # 요청/응답 DTO
│
├── post/                    # 게시글 관리
│   ├── controller/          # PostController
│   ├── service/             # PostService
│   ├── repository/          # PostRepository, CustomPostRepository
│   ├── entity/              # PostEntity, PostImageEntity
│   ├── like/                # 좋아요 기능
│   └── dto/                 # 게시글 관련 DTO
│
├── comment/                 # 댓글 관리
│   ├── entity/              # Comment
│   ├── repository/          # CommentRepository
│   └── dto/                 # 댓글 관련 DTO
│
├── file/                    # 파일 업로드
│   ├── FileController.java  # 파일 업로드 API
│   ├── S3FileStorage.java   # S3 저장소 구현
│   └── LocalFileStorage.java # 로컬 저장소 구현
│
├── config/                  # 설정 클래스
│   ├── SecurityConfig.java  # Spring Security 설정
│   ├── S3Config.java        # AWS S3 설정
│   └── WebMvcConfig.java    # 웹 MVC 설정
│
└── common/                  # 공통 클래스
    ├── ApiResponse.java     # 공통 API 응답 형식
    ├── BaseTimeEntity.java  # 생성/수정 시간 자동 관리
    └── GlobalExceptionHandler.java # 전역 예외 처리
```

## ✨ 주요 기능

### 1. 사용자 인증
- JWT 기반 인증 (Access Token + Refresh Token)
- 회원가입 (이메일, 비밀번호, 닉네임, 프로필 이미지)
- 로그인/로그아웃
- 프로필 수정
- 비밀번호 변경

### 2. 게시글 관리
- 게시글 작성/수정/삭제
- 이미지 다중 업로드
- 무한 스크롤 (커서 기반 페이지네이션)
- 게시글 좋아요
- 조회수 증가

### 3. 댓글 관리
- 댓글 작성/수정/삭제
- 댓글 좋아요

### 4. 파일 관리
- AWS S3를 통한 이미지 업로드
- 프로필 이미지, 게시글 이미지 지원


## 📝 주요 설계 특징

### 1. 계층형 아키텍처
- Controller → Service → Repository 패턴
- DTO를 통한 데이터 전달

### 2. 보안
- Spring Security를 통한 인증/인가
- JWT 토큰 기반 인증
- CSRF 보호
- 비밀번호 BCrypt 암호화

### 3. 파일 저장소
- S3FileStorage: AWS S3 사용 (프로덕션)
- LocalFileStorage: 로컬 파일 시스템 사용 (개발)

### 4. 예외 처리
- GlobalExceptionHandler를 통한 전역 예외 처리
- 공통 ApiResponse 형식으로 일관된 응답

### 5. 무한 스크롤
- 커서 기반 페이지네이션
- QueryDSL을 활용한 동적 쿼리

### 6. QueryDSL 사용
```bash
# Q클래스 생성
./gradlew compileJava
```
