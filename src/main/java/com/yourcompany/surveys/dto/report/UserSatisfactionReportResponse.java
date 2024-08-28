package com.yourcompany.surveys.dto.report;

public record UserSatisfactionReportResponse(
        Long surveyId,
        String surveyTitle,
        Double averageSatisfaction
) {
}
