package com.example.demo1.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String description;
    private String authorNickname;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // 명세서 포맷 준수
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private List<PostBlockDto> blocks;
}