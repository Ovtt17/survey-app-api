package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.question.QuestionOptionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionOptionResponse;
import com.yourcompany.surveys.entity.QuestionOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionOptionMapper {

    public QuestionOptionResponse toResponse(QuestionOption questionOption) {
        return new QuestionOptionResponse(
                questionOption.getId(),
                questionOption.getText(),
                questionOption.getIsCorrect()
        );
    }

    public QuestionOption toEntity(QuestionOptionRequestDTO questionOptionRequest) {
        return QuestionOption.builder()
                .id(questionOptionRequest.id())
                .text(questionOptionRequest.text())
                .isCorrect(questionOptionRequest.isCorrect())
                .build();
    }
}
