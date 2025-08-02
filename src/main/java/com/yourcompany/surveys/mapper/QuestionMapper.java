package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.question.QuestionOptionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionResponse;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.entity.QuestionOption;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.enums.QuestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QuestionMapper {

    private final QuestionOptionMapper questionOptionMapper;

    public QuestionResponse toResponse(Question question) {
        if (question == null)
            throw new NullPointerException("La pregunta no puede ser nula.");

        return new QuestionResponse(
                question.getId(),
                question.getText(),
                question.getType().getValue(),
                question.getIsCorrect(),
                question.getOptions().stream()
                        .map(questionOptionMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    public Question toEntity(QuestionRequestDTO questionRequest, Survey survey) {
        return toEntity(null, questionRequest, survey);
    }

    public Question toEntity(
            Question existingQuestion,
            QuestionRequestDTO request,
            Survey survey
    ) {
        if (request == null)
            throw new NullPointerException("La solicitud de pregunta no puede ser nula.");

        Question question = existingQuestion != null ? existingQuestion : new Question();
        question.setId(request.id());
        question.setText(request.text());
        question.setType(QuestionType.fromValue(request.type()));
        question.setIsCorrect(request.isCorrect());
        question.setSurvey(survey);


        List<QuestionOption> options = request.options()
                .stream()
                .map(optionRequest -> mapOptionRequestToOption(optionRequest, question))
                .toList();

        if (question.getOptions() == null) {
            question.setOptions(new ArrayList<>());
        }

        question.getOptions().clear();
        question.getOptions().addAll(options);

        return question;
    }

    private QuestionOption mapOptionRequestToOption(QuestionOptionRequestDTO optionRequest, Question question) {
        if (optionRequest.id() != null) {
            return question.getOptions().stream()
                    .filter(o -> o.getId().equals(optionRequest.id()))
                    .findFirst()
                    .map(existingOption ->
                            questionOptionMapper.toEntity(existingOption, optionRequest, question)
                    )
                    .orElseGet(() -> questionOptionMapper.toEntity(optionRequest, question));
        } else {
            return questionOptionMapper.toEntity(optionRequest, question);
        }
    }
}
