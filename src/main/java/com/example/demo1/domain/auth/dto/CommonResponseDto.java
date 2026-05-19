package com.example.demo1.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponseDto {
    private boolean isSuccess;
    private String code;
    private String message;
    private Object result; // T 대신 모든 객체를 수용할 수 있는 Object로 변경

    public static CommonResponseDto onSuccess(Object result) {
        return new CommonResponseDto(true, "COMMON200", "요청에 성공하였습니다.", result);
    }
}