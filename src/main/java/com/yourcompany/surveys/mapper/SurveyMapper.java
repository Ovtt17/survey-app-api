package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveyPagedResponse;
import com.yourcompany.surveys.dto.survey.SurveyRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveyResponse;
import com.yourcompany.surveys.dto.survey.SurveySubmissionResponse;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.entity.Survey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SurveyMapper {

    private final UserMapper userMapper;
    private final QuestionMapper questionMapper;

    public SurveySubmissionResponse toSubmissionResponse(Survey survey) {
        if (survey == null) {
            throw new NullPointerException("La encuesta no puede ser nula.");
        }
        return new SurveySubmissionResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                userMapper.toUserResponse(survey.getCreatedBy()),
                survey.getPictureUrl(),
                survey.getQuestions().stream()
                        .map(questionMapper::toResponse)
                        .collect(Collectors.toList()),
                survey.getAverageRating(),
                survey.getRatingCount(),
                survey.getCreatedDate()
        );
    }

    public SurveyResponse toResponse(Survey survey) {
        if (survey == null) {
            throw new NullPointerException("La encuesta no puede ser nula.");
        }
        return new SurveyResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getCreatedBy().getFullName(),
                survey.getCreatedBy().getName(),
                survey.getCreatedBy().getProfilePictureUrl(),
                survey.getAverageRating(),
                survey.getRatingCount(),
                survey.getPictureUrl()
        );
    }

    public SurveyPagedResponse toPagedResponse(Page<Survey> surveys) {
        if (surveys == null) {
            throw new NullPointerException("La página de encuestas no puede ser nula.");
        }
        return new SurveyPagedResponse(
                surveys.stream()
                        .map(this::toResponse)
                        .toList(),
                surveys.getNumber(),
                surveys.getTotalPages()
        );
    }

    /*
     * Converts a SurveyRequestDTO to a Survey entity.
     *
     * @param surveyRequest The SurveyRequestDTO containing the survey data.
     * @return A Survey entity populated with the data from the request.
     */
    public Survey toEntity(SurveyRequestDTO surveyRequest) {
        return toEntity(null, surveyRequest);
    }

    /*
     * Converts a SurveyRequestDTO to a Survey entity, optionally updating an existing Survey.
     *
     * @param existingSurvey The existing Survey to update, or null to create a new one.
     * @param request The SurveyRequestDTO containing the survey data.
     * @return A Survey entity populated with the data from the request.
     */
    public Survey toEntity(Survey existingSurvey, SurveyRequestDTO request) {
        if (request == null) {
            throw new NullPointerException("La solicitud de encuesta no puede ser nula.");
        }

        Survey survey = existingSurvey != null ? existingSurvey : new Survey();
        survey.setId(request.id());
        survey.setTitle(request.title());
        survey.setDescription(request.description());
        survey.setPictureUrl(request.pictureUrl());

        // Preserve the createdBy field if updating an existing survey
        if (existingSurvey != null) {
            survey.setCreatedBy(existingSurvey.getCreatedBy());
        }

        List<Question> questions = request.questions()
                .stream()
                .map(questionRequest -> mapQuestionRequestToQuestion(questionRequest, survey))
                .toList();

        if (survey.getQuestions() == null) {
            survey.setQuestions(new ArrayList<>());
        }

        survey.getQuestions().clear();
        survey.getQuestions().addAll(questions);

        return survey;
    }

    private Question mapQuestionRequestToQuestion(QuestionRequestDTO request, Survey survey) {
        if (request.id() != null) {
            Question existingQuestion = survey.getQuestions().stream()
                    .filter(q -> q.getId().equals(request.id()))
                    .findFirst()
                    .orElse(null);

            return questionMapper.toEntity(existingQuestion, request, survey);
        } else {
            return questionMapper.toEntity(request, survey);
        }
    }
}
