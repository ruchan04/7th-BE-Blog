package com.example.demo1.domain.auth.service;

import com.example.demo1.domain.auth.dto.LoginRequestDto;
import com.example.demo1.domain.auth.dto.SignupRequestDto;
import com.example.demo1.domain.user.entity.User;
import com.example.demo1.domain.user.repository.UserRepository;
import com.example.demo1.global.security.JwtUtil; // [추가]
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // [추가] 토큰 검증 및 생성을 위해 주입이 필요합니다.

    /**
     * 회원가입
     */
    @Transactional
    public void signup(SignupRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .age(dto.getAge())
                .build();

        userRepository.save(user);
    }

    /**
     * 로그인 검증
     */
    @Transactional(readOnly = true)
    public Long login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return Long.valueOf(user.getId());
    }

    /**
     * 토큰 재발급 로직
     */
    @Transactional(readOnly = true) // 데이터 조회 중심이므로 readOnly 추천
    public String reissue(String refreshToken) {
        // 1. Refresh Token 유효성 검사 (서명 및 만료 체크)
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않거나 만료된 리프레시 토큰입니다.");
        }

        // 2. 토큰에서 유저 ID 추출 (JwtUtil에 해당 메서드가 구현되어 있어야 함)
        Long userId = jwtUtil.getUserId(refreshToken);

        // 3. 새로운 Access Token 발급 및 반환
        return jwtUtil.createAccessToken(userId);
    }
}