package com.example.demo1.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostBlockDto {
    private String blockType;
    private String textContent;
    private String imageUrl;
}