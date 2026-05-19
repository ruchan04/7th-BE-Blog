package com.example.demo1.domain.auth.controller;

import com.example.demo1.domain.auth.dto.LoginRequestDto;
import com.example.demo1.domain.auth.dto.SignupRequestDto;
import com.example.demo1.domain.auth.dto.CommonResponseDto;
import com.example.demo1.domain.auth.dto.KakaoLoginResponseDto;
import com.example.demo1.domain.auth.dto.SwaggerOrderResponse;
import com.example.demo1.domain.auth.service.AuthService;
import com.example.demo1.global.security.JwtUtil;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    /**
     * 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto dto) {
        authService.signup(dto);
        return ResponseEntity.ok("회원가입 성공!");
    }

    /**
     * 로그인 API -> success 맨 하단 정렬 적용
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequestDto dto,
            HttpServletResponse response
    ) {
        Map<String, String> tokens = authService.loginAndGetTokens(dto);
        String at = tokens.get("accessToken");
        String rt = tokens.get("refreshToken");

        ResponseCookie cookie = ResponseCookie.from("refreshToken", rt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(14 * 24 * 60 * 60)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", at);

        return ResponseEntity.ok(new SwaggerOrderResponse("AUTH_2000", "로그인 성공", tokenMap));
    }

    /**
     * 토큰 재발급 API -> 💡 컴파일 에러 유발하던 외부 onFailure 제거 후 완벽 대응
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        // 쿠키에 리프레시 토큰이 아예 없는 경우 예외 처리
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new LocalErrorResponse("리프레시 토큰이 존재하지 않거나 만료되었습니다.", "AUTH_4040"));
        }

        try {
            Map<String, String> newTokens = authService.reissueTokens(refreshToken);
            String newAt = newTokens.get("accessToken");
            String newRt = newTokens.get("refreshToken");

            // 1. 새로 발급된 리프레시 토큰을 다시 쿠키에 저장
            ResponseCookie cookie = ResponseCookie.from("refreshToken", newRt)
                    .httpOnly(true)
                    .secure(false) // HTTPS 적용 시 true로 변경
                    .path("/")
                    .maxAge(14 * 24 * 60 * 60)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // 2. 프론트엔드(Swagger) 바디에는 보안을 위해 accessToken만 반환
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("accessToken", newAt);

            return ResponseEntity.ok(new SwaggerOrderResponse("AUTH200_2", "토큰 재발급에 성공하였습니다.", responseBody));

        } catch (AuthService.CustomAuthException ex) {
            HttpStatus status = ex.getErrorCode().equals("AUTH_4040") ? HttpStatus.NOT_FOUND : HttpStatus.UNAUTHORIZED;
            return ResponseEntity.status(status).body(new LocalErrorResponse(ex.getMessage(), ex.getErrorCode()));
        }
    }

    /**
     * 로그아웃 API
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        authService.logout(refreshToken);

        return ResponseEntity.ok(new SwaggerOrderResponse("AUTH_2002", "로그아웃 성공", null));
    }

    /**
     * 카카오 소셜 로그인 콜백 API
     */
    @GetMapping("/oauth/kakao/callback")
    public ResponseEntity<CommonResponseDto> kakaoCallback(@RequestParam("code") String code) {
        KakaoLoginResponseDto loginResult = authService.kakaoLogin(code);
        return ResponseEntity.ok(CommonResponseDto.onSuccess(loginResult));
    }

    // --- 🏁 과제 에러 스크린샷 양식 {"success":false, "message":..., "code":...} 순서 정렬 전용 객체 ---
    @Getter
    @AllArgsConstructor
    @JsonPropertyOrder({"success", "message", "code"})
    public static class LocalErrorResponse {
        private final boolean success = false;
        private final String message;
        private final String code;
    }
}