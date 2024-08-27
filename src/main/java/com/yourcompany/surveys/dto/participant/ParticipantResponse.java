package com.yourcompany.surveys.dto.participant;

import java.time.LocalDateTime;

public record ParticipantResponse(
        Long id,
        Long userId,
        String username,
        Long surveyId,
        String surveyTitle,
        LocalDateTime participatedDate
) {
}
