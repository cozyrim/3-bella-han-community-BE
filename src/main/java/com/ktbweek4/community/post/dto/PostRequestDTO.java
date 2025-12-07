package com.ktbweek4.community.post.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostRequestDTO {
    private String title;
    private String content;
    private List<String> imageUrls; // S3 이미지 URL 리스트 (Lambda 업로드 후 사용)
}
