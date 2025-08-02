package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionResponse;
import com.yourcompany.surveys.dto.survey.SurveyPagedResponse;
import com.yourcompany.surveys.dto.survey.SurveyRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveyResponse;
import com.yourcompany.surveys.dto.survey.SurveySubmissionResponse;
import com.yourcompany.surveys.dto.user.UserResponse;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.enums.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SurveyMapperTest {

    private UserMapper userMapper;
    private QuestionMapper questionMapper;
    private SurveyMapper surveyMapper;

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        questionMapper = mock(QuestionMapper.class);
        surveyMapper = new SurveyMapper(userMapper, questionMapper);
    }

    @Test
    void shouldMapSurveyToSubmissionResponse() {
        User user = User.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .profilePictureUrl("profile.jpg")
                .email("john@example.com")
                .build();

        Question question = Question.builder().id(1L).text("Q1").build();
        Survey survey = Survey.builder()
                .id(10L)
                .title("Test Survey")
                .description("Desc")
                .createdBy(user)
                .pictureUrl("pic.jpg")
                .averageRating(4.5)
                .ratingCount(10L)
                .createdDate(LocalDateTime.now())
                .questions(List.of(question))
                .build();

        UserResponse userResponse = new UserResponse("john_doe", "John", "Doe", "John Doe", "profile.jpg");
        QuestionResponse questionResponse = new QuestionResponse(1L, "Q1", "TEXT", false, List.of());

        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        when(questionMapper.toResponse(question)).thenReturn(questionResponse);

        SurveySubmissionResponse result = surveyMapper.toSubmissionResponse(survey);

        assertEquals(survey.getId(), result.id());
        assertEquals(survey.getTitle(), result.title());
        assertEquals(userResponse, result.creator());
        assertEquals(1, result.questions().size());
        assertEquals(questionResponse, result.questions().get(0));
        assertEquals(survey.getAverageRating(), result.averageRating());
        assertEquals(survey.getRatingCount(), result.ratingCount());
        assertEquals(survey.getCreatedDate(), result.creationDate());
        assertEquals(survey.getPictureUrl(), result.pictureUrl());
    }

    @Test
    void shouldMapSurveyToResponse() {
        User user = User.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .profilePictureUrl("profile.jpg")
                .email("john@example.com")
                .build();

        Survey survey = Survey.builder()
                .id(1L)
                .title("Title")
                .description("Desc")
                .createdBy(user)
                .averageRating(4.0)
                .ratingCount(5L)
                .pictureUrl("pic.png")
                .build();

        SurveyResponse response = surveyMapper.toResponse(survey);

        assertEquals(survey.getId(), response.id());
        assertEquals("Title", response.title());
        assertEquals("john_doe", response.creatorUsername());
        assertEquals("John Doe", response.creatorFullName());
        assertEquals("profile.jpg", response.creatorProfilePicture());
        assertEquals(survey.getAverageRating(), response.averageRating());
        assertEquals(survey.getRatingCount(), response.ratingCount());
        assertEquals(survey.getDescription(), response.description());
    }

    @Test
    void shouldMapSurveyPageToPagedResponse() {
        User user = User.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .profilePictureUrl("profile.jpg")
                .email("john@example.com")
                .build();

        Survey survey = Survey.builder()
                .id(1L)
                .title("Survey")
                .description("Desc")
                .createdBy(user)
                .averageRating(4.0)
                .ratingCount(2L)
                .pictureUrl("pic.jpg")
                .build();

        Page<Survey> page = new PageImpl<>(List.of(survey), PageRequest.of(0, 1), 1);

        // To avoid calling the real method and complicating the test, we use spy to mock toResponse
        SurveyMapper spyMapper = Mockito.spy(surveyMapper);
        SurveyResponse surveyResponse = spyMapper.toResponse(survey);
        doReturn(surveyResponse).when(spyMapper).toResponse(any(Survey.class));

        SurveyPagedResponse response = spyMapper.toPagedResponse(page);

        assertEquals(1, response.surveys().size());
        assertEquals(0, response.page());
        assertEquals(1, response.totalPages());
        assertEquals(surveyResponse, response.surveys().get(0));
    }

    @Test
    void shouldMapSurveyRequestDTOToEntity() {
        // Arrange
        QuestionRequestDTO questionDto = new QuestionRequestDTO(
                null, // id is null for create scenario
                "Sample question?",
                QuestionType.OPCION_UNICA.getValue(),
                false,
                List.of()  // empty options for simplicity
        );

        SurveyRequestDTO dto = new SurveyRequestDTO(
                1L,
                "Test",
                "Description",
                "pic.jpg",
                List.of(questionDto)
        );

        Question mockQuestion = Question.builder()
                .id(1L)
                .text("Sample question?")
                .build();

        // The correct mock must include the survey as an argument
        when(questionMapper.toEntity(eq(questionDto), any(Survey.class))).thenReturn(mockQuestion);

        // Act
        Survey result = surveyMapper.toEntity(dto);

        // Assert
        assertEquals(dto.id(), result.getId());
        assertEquals(1, result.getQuestions().size());
        Question actualQuestion = result.getQuestions().get(0);
        assertNotNull(actualQuestion);
        assertEquals(mockQuestion.getId(), actualQuestion.getId());
        assertEquals(mockQuestion.getText(), actualQuestion.getText());
    }

    @Test
    void shouldMapSurveyRequestDTOToEntityWithExistingQuestion() {
        // Arrange
        QuestionRequestDTO questionDto = new QuestionRequestDTO(
                2L, // existing id
                "Existing question?",
                QuestionType.OPCION_UNICA.getValue(),
                false,
                List.of()
        );

        SurveyRequestDTO dto = new SurveyRequestDTO(
                2L,
                "Test",
                "Description",
                "pic.jpg",
                List.of(questionDto)
        );

        Question existingQuestion = Question.builder()
                .id(2L)
                .text("Existing question?")
                .build();

        Survey existingSurvey = Survey.builder()
                .id(2L)
                .questions(new ArrayList<>(List.of(existingQuestion)))
                .build();

        Question mockQuestion = Question.builder()
                .id(2L)
                .text("Updated question!")
                .build();

        // Mock for overload with existingQuestion
        when(questionMapper.toEntity(eq(existingQuestion), eq(questionDto), any(Survey.class))).thenReturn(mockQuestion);

        // Act
        Survey result = surveyMapper.toEntity(existingSurvey, dto);

        // Assert
        assertEquals(dto.id(), result.getId());
        assertEquals(1, result.getQuestions().size());
        Question actualQuestion = result.getQuestions().get(0);
        assertEquals(mockQuestion.getId(), actualQuestion.getId());
        assertEquals(mockQuestion.getText(), actualQuestion.getText());
    }

    @Test
    void shouldUpdateExistingSurveyWithRequestDTO() {
        // Arrange
        QuestionRequestDTO questionDto = new QuestionRequestDTO(
                3L,
                "Updated question?",
                QuestionType.OPCION_UNICA.getValue(),
                false,
                List.of()
        );
        SurveyRequestDTO dto = new SurveyRequestDTO(
                5L,
                "New title",
                "New description",
                "new.jpg",
                List.of(questionDto)
        );
        Question oldQuestion = Question.builder()
                .id(3L)
                .text("Old question?")
                .build();
        Survey existingSurvey = Survey.builder()
                .id(5L)
                .title("Old title")
                .description("Old description")
                .pictureUrl("old.jpg")
                .questions(new ArrayList<>(List.of(oldQuestion)))
                .build();
        Question updatedQuestion = Question.builder()
                .id(3L)
                .text("Updated question?")
                .build();
        when(questionMapper.toEntity(eq(oldQuestion), eq(questionDto), any(Survey.class))).thenReturn(updatedQuestion);
        // Act
        Survey result = surveyMapper.toEntity(existingSurvey, dto);
        // Assert
        assertEquals(dto.id(), result.getId());
        assertEquals(dto.title(), result.getTitle());
        assertEquals(dto.description(), result.getDescription());
        assertEquals(dto.pictureUrl(), result.getPictureUrl());
        assertEquals(1, result.getQuestions().size());
        assertEquals(updatedQuestion.getText(), result.getQuestions().get(0).getText());
    }

}
