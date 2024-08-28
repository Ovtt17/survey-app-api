package com.yourcompany.surveys.dto.report;

public record UserSurveyParticipationCountResponse(
        Long surveyId,
        String surveyTitle,
        Long userId,
        String username,
        Long participationCount
) {
}
