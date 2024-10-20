package com.yourcompany.surveys.dto.answer;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequestDTO(
        Long id,
        @NotBlank(message = "El id de la encuesta es obligatorio")
        Long surveyId,

        @NotBlank(message = "El id de la pregunta es obligatorio")
        Long questionId,

        @NotBlank(message = "La respuesta es obligatorio")
        String answerText
) {
}
