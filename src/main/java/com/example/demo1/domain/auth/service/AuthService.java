package com.example.demo1.domain.auth.service;

import com.example.demo1.domain.auth.dto.LoginRequestDto;
import com.example.demo1.domain.auth.dto.SignupRequestDto;
import com.example.demo1.domain.auth.dto.KakaoUserInfoDto;
import com.example.demo1.domain.auth.dto.KakaoLoginResponseDto;
import com.example.demo1.domain.user.entity.User;
import com.example.demo1.domain.user.repository.UserRepository;
import com.example.demo1.global.security.JwtUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 💡 과제 규격(재발급/로그아웃 상태 관리)을 위해 서버 메모리에 Refresh Token과 UserId 매핑 저장
    private final ConcurrentHashMap<String, Long> refreshTokenStore = new ConcurrentHashMap<>();

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
     * 로그인 검증 및 자체 토큰 매핑 저장 [6주차 기능 고도화]
     */
    @Transactional
    public Map<String, String> loginAndGetTokens(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        Long userId = Long.valueOf(user.getId());
        String accessToken = jwtUtil.createAccessToken(userId);
        String refreshToken = jwtUtil.createRefreshToken();

        // 재발급 및 로그아웃 검증용 스토어에 보관
        refreshTokenStore.put(refreshToken, userId);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }

    /**
     * 구 버전 로그인 검증 (기존 컨트롤러 호환용 리턴)
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
     * 토큰 재발급 로직 [과제 요구사항 에러 규격 반영 완료]
     */
    @Transactional
    public Map<String, String> reissueTokens(String refreshToken) {
        // 1. [오류 케이스: AUTH_4040] 저장소에 리프레시 토큰이 없거나 만료/로그아웃된 상태
        if (refreshToken == null || !refreshTokenStore.containsKey(refreshToken)) {
            throw new CustomAuthException("AUTH_4040", "리프레시 토큰이 존재하지 않거나 만료되었습니다.");
        }

        // 2. [오류 케이스: AUTH401_2] 토큰 자체의 JWT 유효성 검증 실패
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new CustomAuthException("AUTH401_2", "유효하지 않은 Refresh Token입니다.");
        }

        Long userId = refreshTokenStore.get(refreshToken);

        // 기존 토큰은 만료(삭제) 처리하고 새로 생성하여 발급 (보안 로테이션 정책)
        refreshTokenStore.remove(refreshToken);

        String newAccessToken = jwtUtil.createAccessToken(userId);
        String newRefreshToken = jwtUtil.createRefreshToken();

        refreshTokenStore.put(newRefreshToken, userId);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }

    /**
     * 기존 단일 reissue 메서드 백업 유지
     */
    @Transactional(readOnly = true)
    public String reissue(String refreshToken) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않거나 만료된 리프레시 토큰입니다.");
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        return jwtUtil.createAccessToken(userId);
    }

    /**
     * 로그아웃 로직 [6주차 추가]
     */
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            // 스토어에서 해당 리프레시 토큰 매핑 관계를 삭제하여 재발급을 원천 차단
            refreshTokenStore.remove(refreshToken);
        }
    }

    /**
     * 카카오 소셜 로그인 구현 [7주차 추가 및 리팩토링 완료]
     */
    @Transactional
    public KakaoLoginResponseDto kakaoLogin(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // ====================================================================
        // [1단계: 카카오 인가 코드로 Access Token 요청]
        // ====================================================================
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // 💡 [보안 강화] 카카오 토큰 서버가 403을 뱉지 않도록 User-Agent 장착
        tokenHeaders.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        // 카카오 OAuth API 스펙 요구 파라미터 빌드
        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("grant_type", "authorization_code");
        tokenParams.add("client_id", "50649e64349464e65d13733865026055");
        tokenParams.add("redirect_uri", "http://localhost:8080/auth/oauth/kakao/callback"); // 과제 요구 라우트 규격 설정
        tokenParams.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);

        // 카카오 인증 센터(kauth.kakao.com) 전용 포스트 전송 선로 개통
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                kakaoTokenRequest,
                Map.class
        );

        String kakaoAccessToken = (String) tokenResponse.getBody().get("access_token");

        // ====================================================================
        // [2단계: 발급받은 Access Token으로 카카오 회원 프로필 정보 요청]
        // ====================================================================
        HttpHeaders profileHeaders = new HttpHeaders();
        profileHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // 💡 [인증 완료] 프로필 조회를 위해 인증 컨텍스트(Bearer) 바인딩
        profileHeaders.add("Authorization", "Bearer " + kakaoAccessToken);
        profileHeaders.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(profileHeaders);

        ResponseEntity<KakaoUserInfoDto> userResponse = restTemplate.postForEntity(
                "https://kapi.kakao.com/v2/user/me",
                kakaoProfileRequest,
                KakaoUserInfoDto.class
        );

        KakaoUserInfoDto kakaoUserInfo = userResponse.getBody();
        if (kakaoUserInfo == null || kakaoUserInfo.getKakao_account() == null) {
            throw new RuntimeException("카카오 유저 정보를 불러오지 못했습니다.");
        }

        String email = kakaoUserInfo.getKakao_account().getEmail();
        String nickname = kakaoUserInfo.getKakao_account().getProfile().getNickname();

        // 3. 서비스 DB 회원 가입 혹은 기존 회원 조회 후 객체에 확실히 할당
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = userRepository.save(
                    User.builder()
                            .name(nickname)
                            .email(email)
                            .password(passwordEncoder.encode("OAUTH_BLANK_PASSWORD"))
                            .age(0)
                            .build()
            );
        }

        // 4. Integer 타입을 Long 타입으로 변환하여 안전하게 자체 토큰 생성
        Long userId = Long.valueOf(user.getId());

        String ourAccessToken = jwtUtil.createAccessToken(userId);
        String ourRefreshToken = jwtUtil.createRefreshToken();

        // 소셜 로그인 세션도 재발급 관리를 위해 공유 저장소에 등록
        refreshTokenStore.put(ourRefreshToken, userId);

        return new KakaoLoginResponseDto(ourAccessToken, ourRefreshToken);
    }

    /**
     * 💡 과제 템플릿 에러 핸들링 매핑용 커스텀 런타임 예외 클래스
     */
    @Getter
    public static class CustomAuthException extends RuntimeException {
        private final String errorCode;
        public CustomAuthException(String errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }
    }
}