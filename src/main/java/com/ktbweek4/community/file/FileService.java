package com.ktbweek4.community.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.public-base-url}")
    private String publicBaseUrl;

    // 허용 파일 타입
    private static final String[] ALLOWED_CONTENT_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif"
    };

    /**
     * 파일 저장 (로컬 파일 시스템)
     */
    public StoredFile save(MultipartFile file) throws IOException {
        return save(file, null);
    }

    /**
     * 파일 저장 (폴더 지정 가능)
     */
    public StoredFile save(MultipartFile file, String folder) throws IOException {
        validateFile(file);

        // 확장자 유지
        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = getFileExtension(originalName);

        // 고유 파일명 생성
        String newName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (folder != null && !folder.isBlank()) {
            uploadPath = uploadPath.resolve(folder);
        }
        Files.createDirectories(uploadPath);

        // 파일 저장
        Path target = uploadPath.resolve(newName).normalize().toAbsolutePath();

        // 경로 이탈 방지
        if (!target.startsWith(Paths.get(uploadDir).toAbsolutePath())) {
            throw new SecurityException("잘못된 파일 경로입니다.");
        }

        // 파일 복사
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // 공개 URL 생성
        String relativePath = folder != null && !folder.isBlank()
            ? folder + "/" + newName
            : newName;

        String url = publicBaseUrl.endsWith("/")
            ? publicBaseUrl + relativePath
            : publicBaseUrl + "/" + relativePath;

        log.info("파일 저장 완료: {} -> {}", originalName, url);
        return new StoredFile(newName, url);
    }

    /**
     * URL을 기준으로 파일 삭제
     */
    public void deleteByUrl(String publicUrl) throws IOException {
        if (publicUrl == null || publicUrl.isBlank()) {
            return;
        }

        if (!publicUrl.startsWith(publicBaseUrl)) {
            throw new IllegalArgumentException("해당 URL은 이 서버에서 관리하는 파일이 아닙니다: " + publicUrl);
        }

        // URL에서 파일 경로 추출
        String fileName = publicUrl.substring(publicBaseUrl.length());
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }

        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize().toAbsolutePath();

        // 보안 체크
        if (!filePath.startsWith(Paths.get(uploadDir).toAbsolutePath())) {
            throw new SecurityException("잘못된 파일 삭제 요청입니다.");
        }

        // 파일 존재 여부 확인 후 삭제
        try {
            Files.deleteIfExists(filePath);
            log.info("파일 삭제 완료: {}", fileName);
        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
            throw new IOException("파일 삭제 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 파일 검증
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + contentType);
        }

        // 파일 크기 제한 (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 10MB까지 허용됩니다.");
        }
    }

    /**
     * 허용 파일 타입 체크
     */
    private boolean isAllowedContentType(String contentType) {
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }

    /**
     * 저장 결과 DTO
     */
    public record StoredFile(String storedName, String publicUrl) {}
}
