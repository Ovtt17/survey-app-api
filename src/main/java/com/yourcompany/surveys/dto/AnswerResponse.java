package com.yourcompany.surveys.dto;

public record AnswerResponse(
        Long id,
        Long questionId,
        String answerText
) {
}
