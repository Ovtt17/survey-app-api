package com.yourcompany.surveys.dto;

public record RatingRequestDTO(
        Long id,
        Double rating,
        Long surveyId
) {
}
