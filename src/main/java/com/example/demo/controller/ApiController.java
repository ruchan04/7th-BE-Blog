package com.example.demo.controller;

import com.example.demo.dto.RepeatResponse;
import com.example.demo.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ApiController {
    private final ApiService apiService;

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/string/repeat")
    public RepeatResponse repeatString(@RequestBody Map<String, String> request) {
        String input = request.get("value");
        String processed = apiService.processString(input);
        return new RepeatResponse(input, processed);
    }
}