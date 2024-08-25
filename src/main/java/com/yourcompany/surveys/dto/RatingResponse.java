package com.yourcompany.surveys.dto;

public record RatingResponse(
        Long id,
        Double rating,
        Long surveyId
) {
}
