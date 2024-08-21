package com.yourcompany.surveys.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequestDTO(
        @NotBlank(message = "El id de la pregunta es obligatorio")
        Long questionId,
        @NotBlank(message = "La respuesta es obligatorio")
        String answerText
) {
}
