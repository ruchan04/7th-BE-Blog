package com.example.demo1.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public class SwaggerResponseDto {

    // 💡 [성공 응답] code, message, result를 먼저 배치하고 success를 맨 뒤로 보냅니다.
    @Getter
    @AllArgsConstructor
    @JsonPropertyOrder({"code", "message", "result", "success"})
    public static class Success<T> {
        private final String code;
        private final String message;
        private final T result;
        private final boolean success = true;
    }

    // 💡 [에러 응답] 예시 화면의 {"success": false, "message": "...", "code": "..."} 구조 매핑
    @Getter
    @AllArgsConstructor
    @JsonPropertyOrder({"success", "message", "code"})
    public static class Error {
        private final boolean success = false;
        private final String message;
        private final String code;
    }
}