package com.yourcompany.surveys.dto.report;

public record SurveyReportResponse(
        Long questionId,
        String questionText,
        Long answerId,
        String answerText,
        Long userId,
        String userName
) {
}
