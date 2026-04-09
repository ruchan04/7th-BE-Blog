package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RepeatResponse {
    private String original;
    private String repeated;
}