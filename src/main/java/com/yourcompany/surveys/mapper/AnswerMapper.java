package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.answer.AnswerRequestDTO;
import com.yourcompany.surveys.dto.answer.AnswerResponse;
import com.yourcompany.surveys.entity.Answer;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerMapper {
    public AnswerResponse toResponse(Answer answer) {
        return new AnswerResponse(
                answer.getId(),
                answer.getSurvey().getId(),
                answer.getSurvey().getTitle(),
                answer.getQuestion().getId(),
                answer.getQuestion().getText(),
                answer.getAnswerText(),
                answer.getParticipation().getId()
        );
    }
    public Answer toEntity(AnswerRequestDTO answerRequest, User user) {
        return Answer.builder()
                .id(answerRequest.id())
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
                .user(user)
                .answerText(answerRequest.answerText())
                .build();
    }
}
