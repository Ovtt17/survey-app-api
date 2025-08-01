package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.question.QuestionResponse;
import com.yourcompany.surveys.dto.survey.SurveyPagedResponse;
import com.yourcompany.surveys.dto.survey.SurveyResponse;
import com.yourcompany.surveys.dto.survey.SurveySubmissionResponse;
import com.yourcompany.surveys.dto.user.UserResponse;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.enums.QuestionType;
import com.yourcompany.surveys.handler.exception.SurveyNotFoundException;
import com.yourcompany.surveys.mapper.ParticipationMapper;
import com.yourcompany.surveys.mapper.QuestionMapper;
import com.yourcompany.surveys.mapper.QuestionOptionMapper;
import com.yourcompany.surveys.mapper.SurveyMapper;
import com.yourcompany.surveys.repository.ParticipationRepository;
import com.yourcompany.surveys.repository.SurveyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {
    @InjectMocks
    private SurveyService surveyService;
    @Mock
    private SurveyRepository surveyRepository;
    @Mock
    private SurveyMapper surveyMapper;
    @Mock
    private QuestionMapper questionMapper;
    @Mock
    private ParticipationRepository participationRepository;
    @Mock
    private ParticipationMapper participationMapper;
    @Mock
    private QuestionOptionMapper questionOptionMapper;
    @Mock
    private UserService userService;
    @Mock
    private SurveyImageService surveyImageService;

    @Test
    void should_return_paged_response_when_surveys_exist() {
        int page = 0;
        int size = 2;
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
        Page<Survey> surveyPage = new PageImpl<>(List.of(survey), PageRequest.of(page, size), 1);
        SurveyPagedResponse pagedResponse = mock(SurveyPagedResponse.class);

        when(surveyRepository.findAll(PageRequest.of(page, size))).thenReturn(surveyPage);
        when(surveyMapper.toPagedResponse(surveyPage)).thenReturn(pagedResponse);

        SurveyPagedResponse result = surveyService.getAllSurveys(page, size);
        assertNotNull(result);
        assertEquals(pagedResponse, result);
    }

    @Test
    void should_return_null_when_no_surveys_exist() {
        int page = 0;
        int size = 2;
        Page<Survey> emptyPage = new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
        when(surveyRepository.findAll(PageRequest.of(page, size))).thenReturn(emptyPage);

        SurveyPagedResponse result = surveyService.getAllSurveys(page, size);
        assertNull(result);
    }

    @Test
    void should_return_survey_response_when_searching_by_id() {
        Long surveyId = 1L;
        User user = User.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .profilePictureUrl("profile.jpg")
                .email("john@example.com")
                .build();
        Survey survey = Survey.builder()
                .id(surveyId)
                .title("Test Survey")
                .description("Desc")
                .createdBy(user)
                .createdDate(LocalDateTime.now())
                .averageRating(4.5)
                .ratingCount(10L)
                .pictureUrl("pic.jpg")
                .build();

        SurveyResponse response = new SurveyResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getCreatedBy().getFullName(),
                survey.getCreatedBy().getName(), // username
                survey.getCreatedBy().getProfilePictureUrl(),
                survey.getAverageRating(),
                survey.getRatingCount(),
                survey.getPictureUrl()
        );

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(surveyMapper.toResponse(survey)).thenReturn(response);

        SurveyResponse result = surveyService.findById(surveyId);

        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    void should_throw_exception_when_searching_survey_by_id_and_not_found() {
        Long surveyId = 2L;
        when(surveyRepository.findById(surveyId)).thenReturn(java.util.Optional.empty());

        assertThrows(SurveyNotFoundException.class, () -> surveyService.findById(surveyId));
    }

    @Test
    void should_return_survey_submission_response_when_searching_by_id() {
        Long surveyId = 1L;
        User user = User.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .profilePictureUrl("profile.jpg")
                .email("john@example.com")
                .build();
        Survey survey = Survey.builder()
                .id(surveyId)
                .title("Test Survey")
                .description("Desc")
                .createdBy(user)
                .createdDate(LocalDateTime.now())
                .averageRating(4.5)
                .ratingCount(10L)
                .pictureUrl("pic.jpg")
                .build();

        SurveySubmissionResponse response = new SurveySubmissionResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                new UserResponse(
                        survey.getCreatedBy().getName(),
                        survey.getCreatedBy().getFirstName(),
                        survey.getCreatedBy().getLastName(),
                        survey.getCreatedBy().getFullName(),
                        survey.getCreatedBy().getProfilePictureUrl()
                ),
                survey.getPictureUrl(),
                List.of(
                        new QuestionResponse(
                                1L,
                                "Sample Question",
                                QuestionType.TEXTO.getValue(),
                                false,
                                List.of()
                        )
                ),
                survey.getAverageRating(),
                survey.getRatingCount(),
                survey.getCreatedDate()
        );

        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(surveyMapper.toSubmissionResponse(survey)).thenReturn(response);

        SurveySubmissionResponse result = surveyService.findByIdForSubmission(surveyId);

        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    void should_throw_exception_when_searching_by_id_for_submission_and_not_found() {
        Long surveyId = 99L;
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.empty());

        assertThrows(SurveyNotFoundException.class, () -> surveyService.findByIdForSubmission(surveyId));
    }

    @Test
    void should_return_submission_response_when_found_by_id_for_owner() {
        // Given
        Long surveyId = 1L;
        User user = User.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .profilePictureUrl("profile.jpg")
                .email("john@example.com")
                .build();
        Survey survey = Survey.builder()
                .id(surveyId)
                .title("Test Survey")
                .description("Desc")
                .createdBy(user)
                .createdDate(LocalDateTime.now())
                .averageRating(4.5)
                .ratingCount(10L)
                .pictureUrl("pic.jpg")
                .build();
        SurveySubmissionResponse response = new SurveySubmissionResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                new UserResponse(
                        survey.getCreatedBy().getName(),
                        survey.getCreatedBy().getFirstName(),
                        survey.getCreatedBy().getLastName(),
                        survey.getCreatedBy().getFullName(),
                        survey.getCreatedBy().getProfilePictureUrl()
                ),
                survey.getPictureUrl(),
                List.of(
                        new QuestionResponse(
                                1L,
                                "Sample Question",
                                QuestionType.TEXTO.getValue(),
                                false,
                                List.of()
                        )
                ),
                survey.getAverageRating(),
                survey.getRatingCount(),
                survey.getCreatedDate()
        );
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(surveyRepository.findByIdAndCreatedBy(surveyId, user)).thenReturn(survey);
        when(surveyMapper.toSubmissionResponse(survey)).thenReturn(response);

        // When
        SurveySubmissionResponse result = surveyService.findByIdForOwner(surveyId);

        // Then
        assertNotNull(result);
        assertEquals(response, result);
        verify(userService).getAuthenticatedUser();
        verify(surveyRepository).findByIdAndCreatedBy(surveyId, user);
        verify(surveyMapper).toSubmissionResponse(survey);
    }

    @Test
    void should_throw_exception_when_not_found_by_id_for_owner() {
        // Given
        Long surveyId = 99L;
        User user = User.builder().username("john_doe").build();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(surveyRepository.findByIdAndCreatedBy(surveyId, user)).thenReturn(null);

        // When & Then
        assertThrows(SurveyNotFoundException.class, () -> surveyService.findByIdForOwner(surveyId));
        verify(userService).getAuthenticatedUser();
        verify(surveyRepository).findByIdAndCreatedBy(surveyId, user);
        verify(surveyMapper, never()).toSubmissionResponse(any());
    }
}