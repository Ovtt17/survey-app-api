package com.yourcompany.surveys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SurveyRequestDTO(
        Long id,
        @NotBlank(message = "El título es obligatorio")
        String title,
        @NotBlank(message = "La descripción es obligatoria")
        String description,
        @NotNull(message = "Las preguntas son obligatorias")
        List<QuestionRequestDTO> questions
) {
}