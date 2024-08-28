package com.yourcompany.surveys.dto.rating;

import jakarta.validation.constraints.NotNull;

public record RatingRequestDTO(
        Long id,
        @NotNull(message = "El valor de la calificación no puede estar vacío")
        Double rating,
        @NotNull(message = "El ID de la encuesta no puede estar vacío")
        Long surveyId
) {
}
