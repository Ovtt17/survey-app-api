package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.question.QuestionOptionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionOptionResponse;
import com.yourcompany.surveys.entity.Question;
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


    /**
     * Maps a QuestionOptionRequestDTO to a QuestionOption entity.
     * This method is used when creating a new QuestionOption.
     *
     * @param request The DTO containing the data for the QuestionOption.
     * @param question The Question to which this option belongs.
     * @return A new QuestionOption entity populated with the data from the request DTO.
     */
    public QuestionOption toEntity(QuestionOptionRequestDTO request, Question question) {
        return toEntity(null, request, question);
    }
    /**
     * Maps a QuestionOptionRequestDTO to a QuestionOption entity.
     * If an existing QuestionOption is provided, it updates that entity; otherwise, it creates a new one.
     *
     * @param existingOption The existing QuestionOption to update, or null to create a new one.
     * @param request The DTO containing the data for the QuestionOption.
     * @param question The Question to which this option belongs.
     * @return A QuestionOption entity populated with the data from the request DTO.
     */
    public QuestionOption toEntity(
            QuestionOption existingOption,
            QuestionOptionRequestDTO request,
            Question question
    ) {
        QuestionOption option = existingOption != null ? existingOption : new QuestionOption();
        option.setId(request.id());
        option.setText(request.text());
        option.setIsCorrect(request.isCorrect());
        option.setQuestion(question);

        return option;
    }
}
