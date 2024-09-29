package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.survey.SurveyRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveyResponse;
import com.yourcompany.surveys.dto.survey.SurveySubmissionResponse;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SurveyMapper {

    private final UserMapper userMapper;
    private final QuestionMapper questionMapper;

    public SurveySubmissionResponse toSubmissionResponse(Survey survey) {
        return new SurveySubmissionResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                userMapper.toUserResponse(survey.getCreator()),
                survey.getQuestions().stream()
                        .map(questionMapper::toResponse)
                        .collect(Collectors.toList()),
                survey.getAverageRating(),
                survey.getRatingCount()
        );
    }

    public SurveyResponse toResponse (Survey survey) {
        return new SurveyResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getCreator().getFullName(),
                survey.getCreator().getName(),
                survey.getCreator().getProfilePictureUrl(),
                survey.getAverageRating(),
                survey.getRatingCount()
        );
    }

    public Survey toEntity(SurveyRequestDTO surveyRequest, User user) {
        Survey survey = Survey.builder()
                .id(surveyRequest.id())
                .title(surveyRequest.title())
                .description(surveyRequest.description())
                .creator(user)
                .build();

        survey.setQuestions(surveyRequest.questions().stream()
                .map(questionRequest -> {
                    Question question = questionMapper.toEntity(questionRequest);
                    question.setSurvey(survey);
                    return question;
                })
                .collect(Collectors.toList()));

        return survey;
    }
}
