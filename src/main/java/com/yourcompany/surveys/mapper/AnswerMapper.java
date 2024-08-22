package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.AnswerRequestDTO;
import com.yourcompany.surveys.dto.AnswerResponse;
import com.yourcompany.surveys.entity.Answer;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.entity.Survey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerMapper {
    public AnswerResponse toResponse(Answer answer) {
        return new AnswerResponse(
                answer.getId(),
                answer.getSurvey().getId(),
                answer.getQuestion().getId(),
                answer.getAnswerText()
        );
    }
    public Answer toEntity(AnswerRequestDTO answerRequest) {
        return Answer.builder()
                .survey(
                        Survey.builder()
                                .id(answerRequest.surveyId())
                                .build()
                )
                .question(
                        Question.builder()
                                .id(answerRequest.questionId())
                                .build()
                )
                .answerText(answerRequest.answerText())
                .build();
    }
}
