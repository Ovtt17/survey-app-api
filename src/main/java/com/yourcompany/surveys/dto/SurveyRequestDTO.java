package com.yourcompany.surveys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public record SurveyRequestDTO(
        @NotBlank(message = "El título es obligatorio")
        String title,
        @NotBlank(message = "La descripción es obligatoria")
        String description,
        @NotNull(message = "El ID del creador es obligatorio")
        Long creatorId,
        @NotNull(message = "Las preguntas son obligatorias")
        List<QuestionRequestDTO> questions
) {
}