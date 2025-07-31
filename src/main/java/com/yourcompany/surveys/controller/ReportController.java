package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.service.ExcelReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports")
public class ReportController {
    private final ExcelReportService excelService;

    @GetMapping("/{reportId}/{surveyId}")
    public ResponseEntity<byte[]> getReportBySurvey(@PathVariable Long reportId, @PathVariable Long surveyId) {
        return excelService.generateReport(reportId, surveyId);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<byte[]> getReportWithoutSurvey(@PathVariable Long reportId) {
        return excelService.generateReport(reportId);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getReason());
    }
}
