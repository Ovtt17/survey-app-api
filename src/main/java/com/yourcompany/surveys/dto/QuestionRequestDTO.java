package com.yourcompany.surveys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record QuestionRequestDTO (
        Long id,
        @NotBlank(message = "El contenido de la pregunta es obligatorio")
        String text,
        @NotBlank(message = "El tipo de pregunta es obligatorio")
        String type,
        @NotEmpty(message = "Debe proporcionar al menos una opción para cada pregunta")
        List<QuestionOptionRequestDTO> options
) {
}