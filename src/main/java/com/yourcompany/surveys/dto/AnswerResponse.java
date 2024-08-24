package com.yourcompany.surveys.dto;

public record AnswerResponse(
        Long id,
        Long surveyId,
        Long questionId,
        String answerText
) {
}
