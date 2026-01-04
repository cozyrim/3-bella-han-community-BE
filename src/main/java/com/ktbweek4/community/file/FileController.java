package com.ktbweek4.community.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam MultipartFile file,
                                                      @RequestParam(required = false) String folder) throws IOException {
        log.info("파일 업로드 요청: filename={}, folder={}", file.getOriginalFilename(), folder);

        try {
            var stored = fileService.save(file, folder);
            log.info("파일 업로드 완료: {}", stored.publicUrl());
            return ResponseEntity.ok(Map.of("url", stored.publicUrl()));
        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw e;
        }
    }
}