package com.example.demo1.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {
    private String name;
    private String email;
    private String password;
    private Integer age;
}