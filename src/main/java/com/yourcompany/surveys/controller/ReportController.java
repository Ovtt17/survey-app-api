package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.service.ExcelReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports")
public class ReportController {
    private final ExcelReportService excelService;

    @GetMapping("/{reportId}/{surveyId}")
    public ResponseEntity<byte[]> getReportBySurvey(@PathVariable Long reportId, @PathVariable Long surveyId, Principal principal) {
        return excelService.generateReport(reportId, Optional.of(surveyId), principal);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<byte[]> getReportWithoutSurvey(@PathVariable Long reportId, Principal principal) {
        return excelService.generateReport(reportId, Optional.empty(), principal);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getReason());
    }
}
