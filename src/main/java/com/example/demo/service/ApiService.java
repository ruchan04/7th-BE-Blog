package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class ApiService {
    public String processString(String input) {
        return input + " " + input; // 문자열을 반복해주는 로직
    }
}