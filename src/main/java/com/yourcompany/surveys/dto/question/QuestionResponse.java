package com.yourcompany.surveys.dto.question;

import java.util.List;

public record QuestionResponse(
        Long id,
        String text,
        String type,
        Boolean isCorrect,
        List<QuestionOptionResponse> options
) {
}
