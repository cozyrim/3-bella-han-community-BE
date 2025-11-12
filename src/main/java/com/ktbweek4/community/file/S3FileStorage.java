// src/main/java/.../file/S3FileStorage.java
package com.ktbweek4.community.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileStorage {

    private final S3Client s3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.public-base-url}")
    private String publicBaseUrl; // https://<bucket>.s3.<region>.amazonaws.com

    @Value("${aws.s3.key-prefix:}")
    private String keyPrefix; // 예: "image/", 비어있으면 무시

    /**
     * 파일 저장 (S3)
     * - 기존 LocalFileStorage.save(MultipartFile) 와 동일한 시그니처 유지
     */
    public StoredFile save(MultipartFile file) throws IOException {
        return save(file, null); // 기본: prefix만 사용
    }

    /**
     * 저장 경로를 세분화하고 싶으면 folder 추가 (예: "profile", "post")
     */
    public StoredFile save(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }

        // 확장자 유지
        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) ext = originalName.substring(dot); // ".jpg"

        // 고유 파일명
        String newName = UUID.randomUUID().toString().replace("-", "") + ext;

        // S3 Key 결정: [keyPrefix]/[folder]/[filename]
        StringBuilder keyBuilder = new StringBuilder();
        if (keyPrefix != null && !keyPrefix.isBlank()) {
            keyBuilder.append(trimSlash(keyPrefix)).append("/");
        }
        if (folder != null && !folder.isBlank()) {
            keyBuilder.append(trimSlash(folder)).append("/");
        }
        keyBuilder.append(newName);
        String key = keyBuilder.toString();

        // 업로드
        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3.putObject(put, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // 공개 URL 만들기 (버킷이 공개가 아니라면 presigned URL을 써야 함)
        String url = publicBaseUrl.endsWith("/")
                ? publicBaseUrl + key
                : publicBaseUrl + "/" + key;

        return new StoredFile(key, url); // storedName=key 로 반환
    }

    /**
     * URL을 기준으로 S3 객체 삭제
     * - 기존 deleteByUrl 과 동일한 사용성
     */
    public void deleteByUrl(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) return;

        if (!publicUrl.startsWith(publicBaseUrl)) {
            throw new IllegalArgumentException("해당 URL은 이 서버에서 관리하는 파일이 아닙니다: " + publicUrl);
        }
        // publicBaseUrl 이후의 경로가 key
        String key = publicUrl.substring(publicBaseUrl.length());
        if (key.startsWith("/")) key = key.substring(1);

        DeleteObjectRequest del = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3.deleteObject(del);
    }

    private String trimSlash(String v) {
        String t = v;
        while (t.startsWith("/")) t = t.substring(1);
        while (t.endsWith("/")) t = t.substring(0, t.length() - 1);
        return t;
    }

    public record StoredFile(String storedName, String publicUrl) {}
}