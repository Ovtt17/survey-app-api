package com.yourcompany.surveys.dto.rating;

public record RatingResponse(
        Long id,
        Double rating,
        Long surveyId
) {
}
