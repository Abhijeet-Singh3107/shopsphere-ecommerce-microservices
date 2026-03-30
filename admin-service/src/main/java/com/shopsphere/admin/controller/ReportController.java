package com.shopsphere.admin.controller;


import com.shopsphere.admin.dto.ReportDto;
import com.shopsphere.admin.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ReportDto> getFullReport() {
        return ResponseEntity.ok(reportService.getFullReport());
    }
}
