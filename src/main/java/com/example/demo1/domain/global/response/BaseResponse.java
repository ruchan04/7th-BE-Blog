package com.example.demo1.domain.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {
    private final Boolean isSuccess;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) // 결과값이 null이면 JSON에 포함하지 않음
    private T result;

    // 성공 응답 정적 팩토리 메서드
    public static <T> BaseResponse<T> onSuccess(String code, String message, T result) {
        return new BaseResponse<>(true, code, message, result);
    }

    // 실패 응답 정적 팩토리 메서드
    public static <T> BaseResponse<T> onFailure(String code, String message, T result) {
        return new BaseResponse<>(false, code, message, result);
    }
}