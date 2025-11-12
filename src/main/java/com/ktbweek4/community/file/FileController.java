package com.ktbweek4.community.file;

import com.ktbweek4.community.file.S3FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final S3FileStorage s3FileStorage;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam MultipartFile file,
                                                      @RequestParam(required = false) String folder) throws IOException {
        var stored = s3FileStorage.save(file, folder);
        return ResponseEntity.ok(Map.of("url", stored.publicUrl()));
    }
}