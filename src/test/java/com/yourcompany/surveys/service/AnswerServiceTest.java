package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.answer.AnswerRequestDTO;
import com.yourcompany.surveys.dto.answer.AnswerResponse;
import com.yourcompany.surveys.entity.Answer;
import com.yourcompany.surveys.entity.Participation;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.mapper.AnswerMapper;
import com.yourcompany.surveys.repository.AnswerRepository;
import com.yourcompany.surveys.repository.ParticipationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnswerServiceTest {

    @InjectMocks
    private AnswerService answerService;

    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private AnswerMapper answerMapper;
    @Mock
    private ParticipationRepository participationRepository;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    public void should_successfully_save_answers() {
        // Arrange
        List<AnswerRequestDTO> requestList = List.of(
                new AnswerRequestDTO(1L, 1L, 1L, "Answer 1"),
                new AnswerRequestDTO(2L, 1L, 2L, "Answer 2")
        );

        Survey mockSurvey = Survey.builder().id(1L).title("Encuesta 1").build();
        Participation mockParticipation = Participation.builder()
                .id(100L)
                .survey(mockSurvey)
                .build();
        Answer mockAnswer = new Answer();
        mockAnswer.setId(200L);
        mockAnswer.setAnswerText("Answer 1");
        mockAnswer.setParticipation(mockParticipation);

        when(participationRepository.save(any(Participation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(answerMapper.toEntity(any(AnswerRequestDTO.class))).thenReturn(mockAnswer);
        when(answerRepository.save(any(Answer.class))).thenReturn(mockAnswer);

        // Act
        answerService.save(requestList);

        // Assert
        verify(participationRepository).save(any(Participation.class));
        verify(answerMapper, times(requestList.size())).toEntity(any(AnswerRequestDTO.class));
        verify(answerRepository, times(requestList.size())).save(any(Answer.class));

        // Captor para Participation
        ArgumentCaptor<Participation> participationCaptor = ArgumentCaptor.forClass(Participation.class);
        verify(participationRepository).save(participationCaptor.capture());
        Participation savedParticipation = participationCaptor.getValue();
        assertEquals(mockSurvey.getId(), savedParticipation.getSurvey().getId());

        // Captor para Answer
        ArgumentCaptor<Answer> answerCaptor = ArgumentCaptor.forClass(Answer.class);
        verify(answerRepository, times(requestList.size())).save(answerCaptor.capture());
        List<Answer> savedAnswers = answerCaptor.getAllValues();
        for (Answer ans : savedAnswers) {
            assertEquals(savedParticipation.getId(), ans.getParticipation().getId());
        }
    }

    @Test
    public void should_update_answer_successfully() {
        // Arrange
        Long answerId = 1L;
        AnswerRequestDTO requestDTO = new AnswerRequestDTO(1L, 1L, 1L, "Updated Answer");

        Answer answerEntity = new Answer();
        answerEntity.setId(answerId);
        answerEntity.setAnswerText("Updated Answer");

        AnswerResponse expectedResponse = new AnswerResponse(
                answerId,
                1L,
                "Survey 1",
                1L,
                "Question 1",
                "Updated Answer",
                100L
        );

        when(answerMapper.toEntity(requestDTO)).thenReturn(answerEntity);
        when(answerRepository.save(answerEntity)).thenReturn(answerEntity);
        when(answerMapper.toResponse(answerEntity)).thenReturn(expectedResponse);

        // Act
        AnswerResponse response = answerService.update(answerId, requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(answerMapper).toEntity(requestDTO);
        verify(answerRepository).save(answerEntity);
        verify(answerMapper).toResponse(answerEntity);
    }


    @Test
    public void should_return_all_answers() {
        // Arrange
        Answer mockAnswer1 = new Answer();
        mockAnswer1.setId(1L);
        mockAnswer1.setAnswerText("Answer 1");

        Answer mockAnswer2 = new Answer();
        mockAnswer2.setId(2L);
        mockAnswer2.setAnswerText("Answer 2");

        List<Answer> answers = List.of(mockAnswer1, mockAnswer2);

        when(answerRepository.findAll()).thenReturn(answers);
        when(answerMapper.toResponse(any(Answer.class)))
                .thenReturn(new AnswerResponse(
                        1L,
                        1L,
                        "Survey 1",
                        1L,
                        "Question 1",
                        "Answer 1",
                        100L
                ));

        // Act
        List<AnswerResponse> responses = answerService.findAll();

        // Assert
        assertEquals(answers.size(), responses.size());
        verify(answerRepository, times(1)).findAll();
    }

    @Test
    public void should_return_answer_by_id() {
        // Arrange
        Long answerId = 1L;
        Answer mockAnswer = new Answer();
        mockAnswer.setId(answerId);
        mockAnswer.setAnswerText("Answer 1");

        AnswerResponse mockResponse = new AnswerResponse(
                1L,
                1L,
                "Survey 1",
                1L,
                "Question 1",
                "Answer 1",
                100L
        );

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(mockAnswer));
        when(answerMapper.toResponse(mockAnswer)).thenReturn(mockResponse);

        // Act
        Optional<AnswerResponse> response = answerService.findById(answerId);

        // Assert
        assertTrue(response.isPresent());
        assertEquals(mockResponse, response.get());
        verify(answerRepository).findById(answerId);
        verify(answerMapper).toResponse(mockAnswer);

        // Test not found case
        when(answerRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<AnswerResponse> notFoundResponse = answerService.findById(2L);
        assertTrue(notFoundResponse.isEmpty());
        verify(answerRepository).findById(2L);
    }
}