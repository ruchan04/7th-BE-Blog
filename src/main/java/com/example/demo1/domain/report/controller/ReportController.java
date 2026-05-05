package com.example.demo1.domain.report.controller;

import com.example.demo1.domain.global.response.BaseResponse;
import com.example.demo1.domain.report.dto.ReportRequestDto;
import com.example.demo1.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public BaseResponse<String> report(@RequestBody ReportRequestDto dto) {
        String result = reportService.createReport(dto.getReporterId(), dto.getTargetId());
        return BaseResponse.onSuccess("REP200", result, null);
    }


}