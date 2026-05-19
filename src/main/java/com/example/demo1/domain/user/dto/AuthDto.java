package com.example.demo1.domain.user.dto;

import lombok.*;

public class AuthDto {

    @Getter
    @NoArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class LoginResponse {
        private TokenDto token;
        private UserInfo user;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TokenDto {
        private String accessToken;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UserInfo {
        private Integer id;
        private String email;
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    public static class ReissueRequest {
        private String refreshToken;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ReissueResponse {
        private String accessToken;
        private String refreshToken;
    }
}