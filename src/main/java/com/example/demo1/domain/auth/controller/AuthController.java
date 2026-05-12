package com.example.demo1.domain.auth.controller;

import com.example.demo1.domain.auth.dto.LoginRequestDto;
import com.example.demo1.domain.auth.dto.LoginResponseDto;
import com.example.demo1.domain.auth.dto.SignupRequestDto;
import com.example.demo1.domain.auth.service.AuthService;
import com.example.demo1.global.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap; // [추가]
import java.util.Map;     // [추가]

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
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto dto,
            HttpServletResponse response
    ) {
        Long userId = authService.login(dto);

        String at = jwtUtil.createAccessToken(userId);
        String rt = jwtUtil.createRefreshToken();

        // RT를 쿠키에 담기 (보안 강화)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", rt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(14 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new LoginResponseDto(at));
    }

    /**
     * 토큰 재발급 API
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody Map<String, String> request) {
        // 바디에서 refreshToken 추출
        String refreshToken = request.get("refreshToken");

        // 서비스에서 검증 및 새 AT 발급
        String newAccessToken = authService.reissue(refreshToken);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return ResponseEntity.ok(response);
    }
}