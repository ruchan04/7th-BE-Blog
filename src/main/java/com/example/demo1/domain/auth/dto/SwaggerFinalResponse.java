package com.example.demo1.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SwaggerFinalResponse {

    @Getter
    @AllArgsConstructor
    @JsonPropertyOrder({"code", "message", "result", "success"})
    public static class Success {
        private final String code;
        private final String message;
        private final Object result;
        private final boolean success = true;
    }

    @Getter
    @AllArgsConstructor
    @JsonPropertyOrder({"code", "message", "success"})
    public static class SuccessWithoutResult {
        private final String code;
        private final String message;
        private final boolean success = true;
    }

    @Getter
    @AllArgsConstructor
    @JsonPropertyOrder({"success", "message", "code"})
    public static class Error {
        private final boolean success = false;
        private final String code;
        private final String message;
    }
}