package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionResponse;
import com.yourcompany.surveys.dto.survey.*;
import com.yourcompany.surveys.dto.user.UserResponse;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.enums.QuestionType;
import com.yourcompany.surveys.handler.exception.ImageDeletionException;
import com.yourcompany.surveys.handler.exception.ImageNoContentException;
import com.yourcompany.surveys.handler.exception.SurveyNotFoundException;
import com.yourcompany.surveys.mapper.SurveyMapper;
import com.yourcompany.surveys.repository.SurveyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        verify(surveyRepository).findAll(PageRequest.of(page, size));
        verify(surveyMapper).toPagedResponse(surveyPage);
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

    @Test
    void should_return_surveys_by_user_for_report() {
        // Given
        User user = User.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .profilePictureUrl("profile.jpg")
                .email("john@example.com")
                .build();
        Survey survey1 = Survey.builder()
                .id(1L)
                .title("Survey 1")
                .description("Desc 1")
                .createdBy(user)
                .averageRating(4.0)
                .ratingCount(2L)
                .pictureUrl("pic1.jpg")
                .build();
        Survey survey2 = Survey.builder()
                .id(2L)
                .title("Survey 2")
                .description("Desc 2")
                .createdBy(user)
                .averageRating(4.5)
                .ratingCount(3L)
                .pictureUrl("pic2.jpg")
                .build();
        List<Survey> surveys = List.of(survey1, survey2);
        SurveyResponse response1 = new SurveyResponse(
                survey1.getId(),
                survey1.getTitle(),
                survey1.getDescription(),
                user.getFullName(),
                user.getName(),
                user.getProfilePictureUrl(),
                survey1.getAverageRating(),
                survey1.getRatingCount(),
                survey1.getPictureUrl()
        );
        SurveyResponse response2 = new SurveyResponse(
                survey2.getId(),
                survey2.getTitle(),
                survey2.getDescription(),
                user.getFullName(),
                user.getName(),
                user.getProfilePictureUrl(),
                survey2.getAverageRating(),
                survey2.getRatingCount(),
                survey2.getPictureUrl()
        );
        List<SurveyResponse> responseList = List.of(response1, response2);

        // When
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(surveyRepository.findByCreatedBy(user)).thenReturn(surveys);
        when(surveyMapper.toResponse(survey1)).thenReturn(response1);
        when(surveyMapper.toResponse(survey2)).thenReturn(response2);

        List<SurveyResponse> resultList = surveyService.getByUserForReport();

        // Then
        assertNotNull(resultList);
        assertEquals(responseList, resultList);
        verify(userService).getAuthenticatedUser();
        verify(surveyRepository).findByCreatedBy(user);
        verify(surveyMapper).toResponse(survey1);
        verify(surveyMapper).toResponse(survey2);
    }

    @Test
    void should_save_survey_and_upload_image_if_present() {
        // Arrange
        MultipartFile mockPicture = mock(MultipartFile.class);

        SurveyRequestDTO requestDTO = new SurveyRequestDTO(
                1L,
                "Survey Title",
                "Survey Description",
                null,
                List.of()
        );

        User user = new User();
        user.setUsername("john_doe");

        Survey surveyBeforeSave = Survey.builder()
                .title("Survey Title")
                .description("Survey Description")
                .createdBy(user)
                .build();

        Survey savedSurvey = Survey.builder()
                .title("Survey Title")
                .description("Survey Description")
                .createdBy(user)
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(surveyMapper.toEntity(requestDTO)).thenReturn(surveyBeforeSave);
        when(surveyRepository.save(surveyBeforeSave)).thenReturn(savedSurvey);

        // Act
        String surveyTitleResult = surveyService.save(requestDTO, mockPicture);

        // Assert
        assertEquals(savedSurvey.getTitle(), surveyTitleResult);

        verify(userService).getAuthenticatedUser();
        verify(surveyMapper).toEntity(requestDTO);
        verify(surveyRepository).save(surveyBeforeSave);
        verify(surveyImageService).uploadSurveyPicture(any(SurveyImageRequest.class));
    }

    @Test
    void should_save_survey_without_uploading_image_if_picture_null() {
        // Arrange
        SurveyRequestDTO requestDTO = new SurveyRequestDTO(
                1L,
                "Survey Title",
                "Survey Description",
                null,
                List.of()
        );

        User user = new User();
        user.setUsername("john_doe");

        Survey surveyBeforeSave = Survey.builder()
                .title("Survey Title")
                .description("Survey Description")
                .createdBy(user)
                .build();

        Survey savedSurvey = Survey.builder()
                .title("Survey Title")
                .description("Survey Description")
                .createdBy(user)
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(surveyMapper.toEntity(requestDTO)).thenReturn(surveyBeforeSave);
        when(surveyRepository.save(surveyBeforeSave)).thenReturn(savedSurvey);

        // Act
        String resultTitle = surveyService.save(requestDTO, null);

        // Assert
        assertEquals(savedSurvey.getTitle(), resultTitle);

        verify(userService).getAuthenticatedUser();
        verify(surveyMapper).toEntity(requestDTO);
        verify(surveyRepository).save(surveyBeforeSave);
        verify(surveyImageService, never()).uploadSurveyPicture(any(SurveyImageRequest.class));
    }

    @Test
    void should_update_survey_picture_and_return_new_url() {
        // Given
        Long surveyId = 1L;
        MultipartFile newPicture = mock(MultipartFile.class);
        User user = User.builder()
                .id(10L)
                .username("john_doe")
                .build();
        Survey survey = Survey.builder()
                .id(surveyId)
                .createdBy(user)
                .pictureUrl("oldPic.jpg")
                .build();
        Survey savedSurvey = Survey.builder()
                .id(surveyId)
                .createdBy(user)
                .pictureUrl("newPic.jpg")
                .build();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(surveyImageService.deleteSurveyPicture("oldPic.jpg")).thenReturn(true);
        when(surveyImageService.uploadSurveyPicture(any(SurveyImageRequest.class))).thenReturn(savedSurvey.getPictureUrl());
        when(surveyRepository.save(survey)).thenReturn(savedSurvey);

        // Act
        String resultUrl = surveyService.updateSurveyPicture(surveyId, newPicture);

        // Then
        assertEquals(savedSurvey.getPictureUrl(), resultUrl);
        verify(surveyRepository).findById(surveyId);
        verify(surveyImageService).deleteSurveyPicture("oldPic.jpg");
        verify(surveyImageService).uploadSurveyPicture(any(SurveyImageRequest.class));
        verify(surveyRepository).save(survey);
    }

    @Test
    void should_delete_survey_picture_and_return_success_message() {
        // Given
        Long surveyId = 1L;
        MultipartFile ignored = null; // no picture param
        User user = User.builder()
                .id(10L)
                .username("john_doe")
                .build();
        Survey survey = Survey.builder()
                .id(surveyId)
                .createdBy(user)
                .pictureUrl("pic.jpg")
                .build();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(surveyImageService.deleteSurveyPicture("pic.jpg")).thenReturn(true);
        // Act
        String result = surveyService.deleteSurveyPicture(surveyId);
        // Then
        assertEquals("Foto de la encuesta eliminada correctamente de la encuesta ." + surveyId, result);
        verify(surveyRepository).findById(surveyId);
        verify(surveyImageService).deleteSurveyPicture("pic.jpg");
        verify(surveyRepository).save(survey);
    }

    @Test
    void should_throw_exception_when_deleting_picture_and_none_exists() {
        // Given
        Long surveyId = 2L;
        User user = User.builder()
                .id(10L)
                .username("john_doe")
                .build();
        Survey survey = Survey.builder()
                .id(surveyId)
                .createdBy(user)
                .pictureUrl(null)
                .build();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        // Act & Then
        assertThrows(ImageNoContentException.class, () -> surveyService.deleteSurveyPicture(surveyId));
        verify(surveyRepository).findById(surveyId);
        verify(surveyImageService, never()).deleteSurveyPicture(anyString());
        verify(surveyRepository, never()).save(any());
    }

    @Test
    void should_throw_imageDeletionException_when_delete_picture_fails() {
        // Given
        Long surveyId = 3L;
        User user = User.builder()
                .id(10L)
                .username("john_doe")
                .build();
        Survey survey = Survey.builder()
                .id(surveyId)
                .createdBy(user)
                .pictureUrl("pic.jpg")
                .build();
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));
        when(surveyImageService.deleteSurveyPicture("pic.jpg")).thenReturn(false);
        // Act & Assert
        assertThrows(ImageDeletionException.class, () -> surveyService.deleteSurveyPicture(surveyId));
        verify(surveyRepository).findById(surveyId);
        verify(surveyImageService).deleteSurveyPicture("pic.jpg");
        verify(surveyRepository, never()).save(any());
    }

    @Test
    void should_update_survey_and_delegate_question_replacement() {
        // Given
        Long surveyId = 1L;
        User user = User.builder()
                .id(10L)
                .username("john_doe")
                .build();
        // set up existing survey with one initial question
        Question oldQuestion = new Question();
        oldQuestion.setId(1L);
        oldQuestion.setText("old question");
        Survey existingSurvey = Survey.builder()
                .id(surveyId)
                .title("old title")
                .description("old desc")
                .createdBy(user)
                .pictureUrl("oldPic.jpg")
                .questions(List.of(oldQuestion))
                .build();
        SurveyRequestDTO requestDTO = new SurveyRequestDTO(
                surveyId,
                "new title",
                "new desc",
                "newPic.jpg",
                List.of(
                        new QuestionRequestDTO(
                                null,
                                "q1",
                                QuestionType.TEXTO.getValue(),
                                false,
                                List.of()
                        )
                )
        );
        MultipartFile newPic = mock(MultipartFile.class);

        Question newQuestion = Question.builder()
                .id(1L)
                .text("q1")
                .type(QuestionType.TEXTO)
                .isCorrect(false)
                .options(new ArrayList<>())
                .build();

        Survey updatedSurvey = Survey.builder()
                .id(surveyId)
                .title("new title")
                .description("new desc")
                .createdBy(user)
                .pictureUrl("newPic.jpg")
                .questions(new ArrayList<>(List.of(newQuestion)))
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(existingSurvey));
        when(surveyMapper.toEntity(existingSurvey, requestDTO)).thenReturn(updatedSurvey);
        when(surveyImageService.deleteSurveyPicture("oldPic.jpg")).thenReturn(true);
        when(surveyImageService.uploadSurveyPicture(any(SurveyImageRequest.class))).thenReturn("newPic.jpg");
        when(surveyRepository.save(updatedSurvey)).thenReturn(updatedSurvey);

        // Act
        Long savedSurveyId = surveyService.update(surveyId, requestDTO, newPic);

        // Then
        assertEquals(surveyId, savedSurveyId);
        assertEquals(requestDTO.title(), updatedSurvey.getTitle());
        assertEquals(requestDTO.description(), updatedSurvey.getDescription());
        assertEquals(requestDTO.pictureUrl(), updatedSurvey.getPictureUrl());
        assertEquals(requestDTO.questions().size(), updatedSurvey.getQuestions().size());
        assertEquals(requestDTO.questions().get(0).text(), updatedSurvey.getQuestions().get(0).getText());

        // picture operations
        verify(userService).getAuthenticatedUser();
        verify(surveyRepository).findById(surveyId);
        verify(surveyMapper).toEntity(existingSurvey, requestDTO);
        verify(surveyImageService).deleteSurveyPicture("oldPic.jpg");
        verify(surveyImageService).uploadSurveyPicture(any(SurveyImageRequest.class));
        // survey details updated
        verify(surveyRepository).save(updatedSurvey);
    }

}
