package com.yourcompany.surveys.dto.answer;

public record AnswerResponse(
        Long id,
        Long surveyId,
        String surveyTitle,
        Long questionId,
        String questionText,
        String answerText,
        Long participationId
) {
}
