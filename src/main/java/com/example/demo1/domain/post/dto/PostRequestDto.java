package com.example.demo1.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostRequestDto {

    private Long userId; // 작성 시 필요

    @NotBlank(message = "제목은 비어 있을 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 비어 있을 수 없습니다.")
    private String content;

    @NotBlank(message = "설명은 비어 있을 수 없습니다.")
    private String description;

    private List<PostBlockDto> blocks;
}