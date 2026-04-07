package com.example.demo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // 생성일/수정일 자동 생성을 위해 꼭 필요해요!
@SpringBootApplication // (exclude = ...) 부분을 지워서 DB 자동 설정을 활성화합니다.
public class Demo1Application {
    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }
}