package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionResponse;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.entity.QuestionOption;
import com.yourcompany.surveys.enums.QuestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

    public Question toEntity(QuestionRequestDTO questionRequest) {
        if (questionRequest == null)
            throw new NullPointerException("La solicitud de pregunta no puede ser nula.");

        Question question = Question.builder()
                .id(questionRequest.id())
                .text(questionRequest.text())
                .type(QuestionType.fromValue(questionRequest.type()))
                .isCorrect(questionRequest.isCorrect())
                .build();

        question.setOptions(questionRequest.options().stream()
                .map(optionRequest -> {
                    QuestionOption option = questionOptionMapper.toEntity(optionRequest);
                    option.setQuestion(question);
                    return option;
                })
                .collect(Collectors.toList()));

        return question;
    }
}
