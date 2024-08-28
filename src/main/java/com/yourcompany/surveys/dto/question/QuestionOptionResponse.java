package com.yourcompany.surveys.dto.question;

public record QuestionOptionResponse(
        Long id,
        String text,
        Boolean isCorrect
) {
}
