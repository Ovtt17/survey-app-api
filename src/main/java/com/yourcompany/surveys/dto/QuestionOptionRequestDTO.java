package com.yourcompany.surveys.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionOptionRequestDTO(
        @NotBlank(message = "El contenido de la opción es obligatorio")
        String text,
        Boolean isCorrect
) {

}
