package com.yourcompany.surveys.dto;

import java.time.LocalDateTime;

public record ReviewResponse (
        Long id,
        String title,
        String content,
        Long surveyId,
        RatingResponse rating,
        String author,
        LocalDateTime createdDate
) {
}
