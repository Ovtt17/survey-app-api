package com.yourcompany.surveys.dto.participation;

import java.time.LocalDateTime;

public record ParticipationResponse(
        Long id,
        Long userId,
        String username,
        String profilePictureUrl,
        Long surveyId,
        String surveyTitle,
        LocalDateTime participatedDate
) {
}
