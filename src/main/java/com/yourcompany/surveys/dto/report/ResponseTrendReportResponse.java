package com.yourcompany.surveys.dto.report;

public record ResponseTrendReportResponse(
        Long questionId,
        String questionText,
        String answerText,
        Long frequency
) {
}