package com.example.demo1.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReissueRequestDto {
    private String refreshToken;
}