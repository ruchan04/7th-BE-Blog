package com.example.demo1.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;
    private KakaoAccount kakao_account;

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        private Profile profile;
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class Profile {
        private String nickname;
    }
}