# 따끈따끈 식탁🍽️ - 백엔드

음식 커뮤니티 플랫폼의 백엔드 서버입니다.

## 📋 목차

- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [주요 기능](#주요-기능)
- [API 엔드포인트](#api-엔드포인트)
- [환경 설정](#환경-설정)
- [실행 방법](#실행-방법)
- [데이터베이스](#데이터베이스)

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

## 🔌 API 엔드포인트

### 인증 (`/api/v1/auth`)
- `POST /login` - 로그인
- `POST /logout` - 로그아웃

### 사용자 (`/api/v1/users`)
- `POST /signup` - 회원가입 (JSON)
- `POST /signup` - 회원가입 (Multipart - 프로필 이미지 포함)
- `GET /me` - 내 정보 조회
- `PATCH /me` - 프로필 수정
- `PATCH /me/password` - 비밀번호 변경
- `GET /check-email` - 이메일 중복 확인
- `GET /check-nickname` - 닉네임 중복 확인

### 게시글 (`/api/v1/posts`)
- `GET /posts` - 게시글 목록 (무한 스크롤)
- `GET /posts/{id}` - 게시글 상세
- `POST /posts` - 게시글 작성
- `PATCH /posts/{id}` - 게시글 수정
- `DELETE /posts/{id}` - 게시글 삭제
- `POST /posts/{id}/likes` - 좋아요
- `DELETE /posts/{id}/likes` - 좋아요 취소

### 댓글 (`/api/v1/posts/{postId}/comments`)
- `GET /comments` - 댓글 목록
- `POST /comments` - 댓글 작성
- `PATCH /comments/{id}` - 댓글 수정
- `DELETE /comments/{id}` - 댓글 삭제

### 파일 (`/api/v1/files`)
- `POST /upload` - 파일 업로드 (S3)

### API 문서
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## ⚙️ 환경 설정

### 필수 환경 변수

```bash
# 데이터베이스
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/community
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# JWT
JWT_SECRET=your-secret-key
JWT_ACCESS_MS=3600000        # 1시간
JWT_REFRESH_MS=1209600000   # 14일

# AWS S3
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_REGION=ap-northeast-2
AWS_S3_BUCKET_NAME=your-bucket-name

# 파일 업로드
UPLOAD_DIR=/path/to/uploads
PUBLIC_BASE_URL=http://localhost:8080/files
```

### 환경별 설정 파일

- `application.yml` - 기본 설정
- `application-dev.yml` - 개발 환경
- `application-prod.yml` - 프로덕션 환경
- `application-test.yml` - 테스트 환경

## 🚀 실행 방법

### 1. 사전 요구사항
- Java 21 이상
- MySQL 8.0 이상
- Gradle 8.0 이상

### 2. 데이터베이스 설정
```sql
CREATE DATABASE community CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 환경 변수 설정
`.env` 파일을 생성하거나 환경 변수를 설정합니다.

### 4. 실행
```bash
# Gradle Wrapper 사용
./gradlew bootRun

# 또는 JAR 빌드 후 실행
./gradlew build
java -jar build/libs/community-0.0.1-SNAPSHOT.jar
```

### 5. 테스트 실행
```bash
./gradlew test
```

## 🗄 데이터베이스

### 주요 테이블

- **users** - 사용자 정보
- **posts** - 게시글
- **postimages** - 게시글 이미지
- **comments** - 댓글
- **post_likes** - 게시글 좋아요
- **refresh_token** - 리프레시 토큰

### 엔티티 관계

```
User (1) ──< (N) Post
User (1) ──< (N) Comment
User (1) ──< (N) PostLike
Post (1) ──< (N) PostImage
Post (1) ──< (N) Comment
```

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

## 🔧 개발 팁

### QueryDSL 사용
```bash
# Q클래스 생성
./gradlew compileJava
```

### 로그 레벨 조정
`application.yml`에서 로그 레벨을 조정할 수 있습니다:
```yaml
logging:
  level:
    org.hibernate.SQL: debug
    org.springframework.security: DEBUG
```

## 📚 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Security 공식 문서](https://spring.io/projects/spring-security)
- [QueryDSL 공식 문서](https://querydsl.com/)

## 📄 라이선스

이 프로젝트는 교육 목적으로 작성되었습니다.
