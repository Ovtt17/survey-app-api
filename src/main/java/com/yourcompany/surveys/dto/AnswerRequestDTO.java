package com.yourcompany.surveys.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record AnswerRequestDTO(
        Long id,
        @NotBlank(message = "El id de la encuesta es obligatorio")
        Long surveyId,

        @NotBlank(message = "El id de la pregunta es obligatorio")
        Long questionId,

        @NotBlank(message = "La respuesta es obligatorio")
        String answerText,

        @NotBlank(message = "La calificación es obligatorio")
        @DecimalMin(value = "0.0", message = "La calificación debe ser mayor o igual a 0")
        @DecimalMax(value = "5.0", message = "La calificación debe ser menor o igual a 5")
        Double rating
) {
}
