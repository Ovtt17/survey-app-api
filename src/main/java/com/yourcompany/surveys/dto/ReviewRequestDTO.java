package com.yourcompany.surveys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestDTO (
        Long id,
        @NotBlank(message = "El título es obligatorio")
        String title,
        @NotBlank(message = "El contenido es obligatorio")
        String content,
        @NotNull(message = "El id de la encuesta es obligatorio")
        Long surveyId,
        @NotNull(message = "El rating es obligatorio")
        RatingRequestDTO rating,
        @NotBlank(message = "El nombre de usuario del creador es obligatorio")
        String author
) {
}
