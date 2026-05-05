package com.example.demo1.domain.post.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostStatus {
    ACTIVE("활성화"),
    HIDDEN("숨김");

    private final String description;
}