package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.question.QuestionOptionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionResponse;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.entity.QuestionOption;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.enums.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class QuestionMapperTest {

    private QuestionMapper questionMapper;

    @BeforeEach
    void setUp() {
        QuestionOptionMapper questionOptionMapper = new QuestionOptionMapper();
        questionMapper = new QuestionMapper(questionOptionMapper);
    }

    @Test
    void shouldMapQuestionEntityToResponse() {
        // given
        Question question = Question.builder()
                .id(1L)
                .text("What is your favorite color?")
                .type(QuestionType.OPCION_UNICA)
                .isCorrect(false)
                .build();

        List<QuestionOption> options = List.of(
                QuestionOption.builder()
                        .id(1L)
                        .text("Red")
                        .isCorrect(false)
                        .question(question)
                        .build(),
                QuestionOption.builder()
                        .id(2L)
                        .text("Blue")
                        .isCorrect(false)
                        .question(question)
                        .build()
        );
        question.setOptions(options);

        // when
        QuestionResponse response = questionMapper.toResponse(question);

        // then
        assertNotNull(response);
        assertEquals(question.getId(), response.id());
        assertEquals(question.getText(), response.text());
        assertEquals(question.getType().getValue(), response.type());
        assertEquals(question.getIsCorrect(), response.isCorrect());
        assertNotNull(response.options());
        assertEquals(question.getOptions().size(), response.options().size());
    }

    @Test
    void shouldMapQuestionRequestDTOToEntity() {
        // given
        QuestionOptionRequestDTO option1 = new QuestionOptionRequestDTO(1L, "Option 1", false);
        QuestionOptionRequestDTO option2 = new QuestionOptionRequestDTO(2L, "Option 2", true);
        QuestionRequestDTO requestDTO = new QuestionRequestDTO(
                10L,
                "Sample question?",
                QuestionType.OPCION_UNICA.getValue(),
                false,
                List.of(option1, option2)
        );

        Survey survey = new Survey();
        survey.setId(100L);
        // when
        Question entity = questionMapper.toEntity(requestDTO, survey);
        // then
        assertEquals(requestDTO.id(), entity.getId());
        assertEquals(requestDTO.text(), entity.getText());
        assertEquals(QuestionType.OPCION_UNICA, entity.getType());
        assertEquals(requestDTO.isCorrect(), entity.getIsCorrect());
        assertNotNull(entity.getOptions());
        assertEquals(2, entity.getOptions().size());

        entity.getOptions().forEach(option -> {
            int index = entity.getOptions().indexOf(option);
            QuestionOptionRequestDTO optionDTO = requestDTO.options().get(index);
            assertEquals(optionDTO.id(), option.getId());
            assertEquals(optionDTO.text(), option.getText());
            assertEquals(optionDTO.isCorrect(), option.getIsCorrect());
            assertEquals(entity, option.getQuestion());
        });
    }
}
