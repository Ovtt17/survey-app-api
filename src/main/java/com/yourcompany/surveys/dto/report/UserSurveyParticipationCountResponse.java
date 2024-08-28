package com.yourcompany.surveys.dto.report;

public record UserSurveyParticipationCountResponse(
        Long surveyId,
        String surveyTitle,
        Long participationCount
) {
}
