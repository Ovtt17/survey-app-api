package com.yourcompany.surveys.dto.survey;

import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

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