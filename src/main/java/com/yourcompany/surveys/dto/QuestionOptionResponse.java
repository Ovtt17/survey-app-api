package com.yourcompany.surveys.dto;

public record QuestionOptionResponse(
        Long id,
        String text,
        Boolean isCorrect
) {
}
