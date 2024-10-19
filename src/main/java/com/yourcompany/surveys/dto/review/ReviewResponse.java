package com.yourcompany.surveys.dto.review;

import com.yourcompany.surveys.dto.rating.RatingResponse;

import java.time.LocalDateTime;

public record ReviewResponse (
        Long id,
        String title,
        String content,
        Long surveyId,
        String surveyTitle,
        RatingResponse rating,
        String authorUsername,
        String authorPicture,
        LocalDateTime createdDate
) {
}
