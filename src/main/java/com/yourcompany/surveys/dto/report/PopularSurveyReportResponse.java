package com.yourcompany.surveys.dto.report;

public record PopularSurveyReportResponse(
        Long surveyId,
        String surveyTitle,
        Long participationCount
) {
}
