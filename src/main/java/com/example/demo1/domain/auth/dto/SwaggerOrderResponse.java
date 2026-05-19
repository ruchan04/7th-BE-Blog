package com.example.demo1.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"code", "message", "result", "success"}) // 💡 success를 무조건 맨 아래로 보내는 마법의 어노테이션
public class SwaggerOrderResponse {
    private final String code;
    private final String message;
    private final Object result;
    private final boolean success = true;

    public SwaggerOrderResponse(String code, String message, Object result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }
}