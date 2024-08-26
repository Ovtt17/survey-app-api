package com.yourcompany.surveys.dto.question;

import java.util.List;

public record QuestionResponse(
        Long id,
        String text,
        String type,
        List<QuestionOptionResponse> options
) {
}
