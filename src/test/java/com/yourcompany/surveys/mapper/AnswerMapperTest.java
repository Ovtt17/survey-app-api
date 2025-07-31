package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.answer.AnswerRequestDTO;
import com.yourcompany.surveys.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnswerMapperTest {

    private AnswerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AnswerMapper();
    }

    @Test
    public void should_map_answer_request_to_answer() {
        AnswerRequestDTO request = new AnswerRequestDTO(
                1L,
                100L,
                200L,
                "This is an answer"
        );

        User user = User.builder().id(10L).build();
        Answer answer = mapper.toEntity(request);
        answer.setCreatedBy(user);

        assertEquals(answer.getId(), request.id());
        assertEquals(answer.getSurvey().getId(), request.surveyId());
        assertEquals(answer.getQuestion().getId(), request.questionId());
        assertEquals(answer.getAnswerText(), request.answerText());
        assertNotNull(answer.getCreatedBy());
    }

    @Test
    public void should_map_answer_to_answer_response() {
        Answer answer = new Answer();
        answer.setId(100L);
        answer.setAnswerText("This is an answer");

        User user = User.builder().id(10L).build();
        Survey survey = Survey.builder()
                .id(1L)
                .title("Survey Title")
                .build();
        Question question = Question.builder()
                .id(200L)
                .text("Question Text")
                .build();
        Participation participation = Participation.builder()
                .id(300L)
                .build();

        answer.setCreatedBy(user);
        answer.setSurvey(survey);
        answer.setQuestion(question);
        answer.setParticipation(participation);

        var response = mapper.toResponse(answer);

        assertEquals(response.id(), answer.getId());
        assertEquals(response.surveyId(), answer.getSurvey().getId());
        assertEquals(response.surveyTitle(), answer.getSurvey().getTitle());
        assertEquals(response.questionId(), answer.getQuestion().getId());
        assertEquals(response.questionText(), answer.getQuestion().getText());
        assertEquals(response.answerText(), answer.getAnswerText());
        assertEquals(response.participationId(), answer.getParticipation().getId());
    }
}