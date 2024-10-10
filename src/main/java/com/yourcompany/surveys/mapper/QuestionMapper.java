package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionResponse;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.entity.QuestionOption;
import com.yourcompany.surveys.entity.QuestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QuestionMapper {

    private final QuestionOptionMapper questionOptionMapper;

    public QuestionResponse toResponse (Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getText(),
                question.getType().getValue(),
                question.getOptions().stream()
                        .map(questionOptionMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    public Question toEntity(QuestionRequestDTO questionRequest) {
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
