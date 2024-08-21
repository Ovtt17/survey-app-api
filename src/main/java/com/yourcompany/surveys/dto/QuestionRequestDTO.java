package com.yourcompany.surveys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public record QuestionRequestDTO (
        @NotBlank(message = "El contenido de la pregunta es obligatorio")
        String text,
        @NotBlank(message = "El tipo de pregunta es obligatorio")
        String type,
        @NotEmpty(message = "Debe proporcionar al menos una opción para cada pregunta")
        List<QuestionOptionRequestDTO> options
) {
}