package com.yourcompany.surveys.dto.survey;

public record SurveyResponse (
        Long id,
        String title,
        String description,
        String creatorFullName,
        String creatorUsername,
        Double averageRating,
        Long ratingCount
) {
}
