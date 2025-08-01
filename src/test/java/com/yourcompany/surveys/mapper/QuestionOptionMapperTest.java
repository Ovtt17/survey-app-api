package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.question.QuestionOptionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionOptionResponse;
import com.yourcompany.surveys.entity.QuestionOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionOptionMapperTest {

    private QuestionOptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new QuestionOptionMapper();
    }

    @Test
    void shouldMapQuestionOptionRequestToEntity() {
        // given
        QuestionOptionRequestDTO request = new QuestionOptionRequestDTO(
                1L,
                "Option 1",
                false
        );

        // when
        QuestionOption entity = mapper.toEntity(request);

        // then
        assertNotNull(entity);
        assertEquals(request.id(), entity.getId());
        assertEquals(request.text(), entity.getText());
        assertEquals(request.isCorrect(), entity.getIsCorrect());
    }

    @Test
    void shouldMapQuestionOptionEntityToResponse() {
        // given
        QuestionOption option = QuestionOption.builder()
                .id(1L)
                .text("Option 1")
                .isCorrect(false)
                .build();

        // when
        QuestionOptionResponse response = mapper.toResponse(option);

        // then
        assertNotNull(response);
        assertEquals(option.getId(), response.id());
        assertEquals(option.getText(), response.text());
        assertEquals(option.getIsCorrect(), response.isCorrect());
    }
}