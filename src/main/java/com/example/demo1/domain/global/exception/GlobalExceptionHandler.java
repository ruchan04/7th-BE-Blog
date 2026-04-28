package com.example.demo1.domain.global.exception;

import com.example.demo1.domain.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 유효성 검사 실패 처리 (제목/내용 빈 값 등 - 코드 4002) [cite: 449, 479]
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.onFailure("4002", "잘못된 요청입니다.", errors));
    }

    // 2. 리소스를 찾을 수 없을 때 처리 (게시글 없음 - 코드 4040) [cite: 436, 480]
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BaseResponse<Map<String, Long>>> handleNotFoundException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.onFailure("4040", e.getMessage(), null));
    }
}