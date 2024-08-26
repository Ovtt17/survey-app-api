package com.yourcompany.surveys.dto.answer;

public record AnswerResponse(
        Long id,
        Long surveyId,
        Long questionId,
        String answerText
) {
}
