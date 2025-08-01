package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.question.QuestionOptionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.enums.QuestionType;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class QuestionMapperTest {

    private final QuestionOptionMapper questionOptionMapper = new QuestionOptionMapper();
    private final QuestionMapper questionMapper = new QuestionMapper(questionOptionMapper);

    @Test
    void shouldMapQuestionRequestDTOToEntity() {
        // given
        QuestionOptionRequestDTO option1 = new QuestionOptionRequestDTO(1L, "Option 1", false);
        QuestionOptionRequestDTO option2 = new QuestionOptionRequestDTO(2L, "Option 2", true);
        QuestionRequestDTO requestDTO = new QuestionRequestDTO(10L, "Sample question?", "SINGLE", false, List.of(option1, option2));
        // when
        Question entity = questionMapper.toEntity(requestDTO);
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
