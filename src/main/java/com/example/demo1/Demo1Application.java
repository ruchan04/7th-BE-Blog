package com.example.demo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
// [★핵심] 스프링에게 이 패키지 하위의 모든 레포지토리와 엔티티를 무조건 다 뒤지라고 명령합니다.
@EnableJpaRepositories(basePackages = "com.example.demo1")
@EntityScan(basePackages = "com.example.demo1")
public class Demo1Application {
    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }
}