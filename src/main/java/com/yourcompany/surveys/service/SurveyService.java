package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.dto.question.QuestionOptionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.dto.survey.*;
import com.yourcompany.surveys.entity.*;
import com.yourcompany.surveys.handler.exception.ImageDeletionException;
import com.yourcompany.surveys.handler.exception.ImageNoContentException;
import com.yourcompany.surveys.handler.exception.SurveyNotFoundException;
import com.yourcompany.surveys.handler.exception.UnauthorizedException;
import com.yourcompany.surveys.mapper.ParticipationMapper;
import com.yourcompany.surveys.mapper.QuestionMapper;
import com.yourcompany.surveys.mapper.QuestionOptionMapper;
import com.yourcompany.surveys.mapper.SurveyMapper;
import com.yourcompany.surveys.repository.ParticipationRepository;
import com.yourcompany.surveys.repository.SurveyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final QuestionMapper questionMapper;
    private final ParticipationRepository participationRepository;
    private final ParticipationMapper participationMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final UserService userService;
    private final SurveyImageService surveyImageService;

    public SurveyPagedResponse getAllSurveys(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Survey> surveys = surveyRepository.findAll(pageable);
        return surveyMapper.toPagedResponse(surveys);
    }

    public SurveyResponse findById(Long id) {
        Optional<Survey> survey = surveyRepository.findById(id);
        if (survey.isEmpty()) {
            throw new SurveyNotFoundException("Encuesta no encontrada.");
        }
        return surveyMapper.toResponse(survey.get());
    }

    public SurveySubmissionResponse findByIdForSubmission(Long id) {
        Optional<Survey> survey = surveyRepository.findById(id);
        if (survey.isEmpty()) {
            throw new SurveyNotFoundException("Encuesta no encontrada.");
        }
        return surveyMapper.toSubmissionResponse(survey.get());
    }

    public SurveySubmissionResponse findByIdForOwner(Long id, Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        Survey survey = surveyRepository.findByIdAndCreator(id, user);
        if (survey == null) {
            throw new SurveyNotFoundException("Encuesta no encontrada.");
        }
        return surveyMapper.toSubmissionResponse(survey);
    }

    public List<SurveyResponse> getByUserForReport(Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        List<Survey> surveys = surveyRepository.findByCreator(user);
        return surveys.stream()
                .map(surveyMapper::toResponse)
                .toList();
    }

    public SurveyPagedResponse getByUserWithPaging(
            Principal principal,
            int page,
            int size
    ) {
        User user = userService.getUserFromPrincipal(principal);
        Pageable pageable = PageRequest.of(page, size);
        Page<Survey> surveys = surveyRepository.findByCreator(user, pageable);
        return surveyMapper.toPagedResponse(surveys);
    }

    public SurveyPagedResponse getByUsernameWithPaging(
            String username,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Survey> surveys = surveyRepository.findByCreatorUsername(username, pageable);
        return surveyMapper.toPagedResponse(surveys);
    }

    @Transactional
    public String save(SurveyRequestDTO surveyRequest, MultipartFile picture, Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        Survey survey = surveyMapper.toEntity(surveyRequest, user);
        processSurveyPictureIfPresent(picture, survey, user);
        Survey savedSurvey = surveyRepository.save(survey);
        return savedSurvey.getTitle();
    }

    @Transactional
    public String updateSurveyPicture(Long surveyId, MultipartFile newPicture, Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new SurveyNotFoundException("Encuesta no encontrada con ID: " + surveyId));
        validateSurveyOwnership(survey, user);
        deleteExistingPictureIfPresent(survey);
        processSurveyPictureIfPresent(newPicture, survey, user);
        Survey surveyWithPictureModified = surveyRepository.save(survey);
        return surveyWithPictureModified.getPictureUrl();
    }

    private void processSurveyPictureIfPresent(MultipartFile newPicture, Survey survey, User user) {
        if (newPicture != null) {
            SurveyImageRequest imageRequest = SurveyImageRequest.builder()
                    .picture(newPicture)
                    .surveyId(survey.getId())
                    .username(user.getUsername())
                    .imageType(ImageType.SURVEY_PICTURE)
                    .build();
            uploadAndSetSurveyPicture(imageRequest, survey);
        }
    }

    private void uploadAndSetSurveyPicture(SurveyImageRequest imageRequest, Survey survey) {
        String imageUrl = surveyImageService.uploadSurveyPicture(imageRequest);
        survey.setPictureUrl(imageUrl);
    }

    private void deleteExistingPictureIfPresent(Survey survey) {
        String pictureUrl = survey.getPictureUrl();
        if (pictureUrl != null && !pictureUrl.isEmpty()) {
            deleteExistingPicture(pictureUrl);
            survey.setPictureUrl(null);
        }
    }

    private void deleteExistingPicture(String pictureUrl) {
        try {
            boolean isPictureDeleted = surveyImageService.deleteSurveyPicture(pictureUrl);
            if (!isPictureDeleted) {
                throw new ImageDeletionException("Error al eliminar la foto de la encuesta.");
            }
        } catch (Exception e) {
            throw new ImageDeletionException("Error inesperado al eliminar la foto de la encuesta: " + e.getMessage(), e);
        }
    }

    public String deleteSurveyPicture(Long surveyId, Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new SurveyNotFoundException("Encuesta no encontrada con ID: " + surveyId));
        validateSurveyOwnership(survey, user);

        String pictureUrl = survey.getPictureUrl();
        if (pictureUrl != null && !pictureUrl.isEmpty()) {
            throw new ImageNoContentException("La encuesta no tiene foto.");
        }

        deleteExistingPicture(survey.getPictureUrl());
        survey.setPictureUrl(null);
        surveyRepository.save(survey);
        return "Foto de la encuesta eliminada correctamente de la encuesta ." + surveyId;
    }

    private void validateSurveyOwnership(Survey survey, User user) {
        if (!survey.getCreator().equals(user)) {
            throw new UnauthorizedException("No tienes permiso para actualizar esta encuesta.");
        }
    }

    @Transactional
    public Long update(
            Long surveyId,
            SurveyRequestDTO surveyRequest,
            MultipartFile picture,
            Principal principal
    ) {
        User user = userService.getUserFromPrincipal(principal);
        Survey existingSurvey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new SurveyNotFoundException("Encuesta no encontrada con ID: " + surveyId));
        validateSurveyOwnership(existingSurvey, user);
        deleteExistingPictureIfPresent(existingSurvey);
        processSurveyPictureIfPresent(picture, existingSurvey, user);
        updateSurveyDetails(existingSurvey, surveyRequest);
        updateExistingQuestions(existingSurvey, surveyRequest);
        surveyRepository.save(existingSurvey);
        return existingSurvey.getId();
    }

    private void updateSurveyDetails(Survey existingSurvey, SurveyRequestDTO surveyRequest) {
        existingSurvey.setTitle(surveyRequest.title());
        existingSurvey.setDescription(surveyRequest.description());
    }

    private void updateExistingQuestions(Survey existingSurvey, SurveyRequestDTO surveyRequest) {
        Map<Long, QuestionRequestDTO> requestQuestionsMap = surveyRequest.questions().stream()
                .collect(Collectors.toMap(QuestionRequestDTO::id, q -> q));

        Iterator<Question> existingQuestionsIterator = existingSurvey.getQuestions().iterator();
        while (existingQuestionsIterator.hasNext()) {
            Question existingQuestion = existingQuestionsIterator.next();

            if (requestQuestionsMap.containsKey(existingQuestion.getId())) {
                QuestionRequestDTO questionRequest = requestQuestionsMap.get(existingQuestion.getId());
                existingQuestion.setText(questionRequest.text());
                existingQuestion.setType(QuestionType.fromValue(questionRequest.type()));
                existingQuestion.setIsCorrect(questionRequest.isCorrect());
                updateOptions(existingQuestion, questionRequest);
                requestQuestionsMap.remove(existingQuestion.getId());
            } else {
                existingQuestionsIterator.remove();
            }
        }

        addNewQuestionsToExistingSurvey(existingSurvey, requestQuestionsMap);
    }

    private void addNewQuestionsToExistingSurvey(Survey existingSurvey, Map<Long, QuestionRequestDTO> remainingQuestions) {
        remainingQuestions.values().forEach(questionRequest -> {
            Question newQuestion = questionMapper.toEntity(questionRequest);
            newQuestion.setSurvey(existingSurvey);
            existingSurvey.getQuestions().add(newQuestion);
        });
    }

    private void updateOptions(Question existingQuestion, QuestionRequestDTO questionRequest) {
        Map<Long, QuestionOptionRequestDTO> requestOptionsMap = questionRequest.options().stream()
                .filter(option -> option.id() != null)
                .collect(Collectors.toMap(QuestionOptionRequestDTO::id, o -> o));

        Iterator<QuestionOption> existingOptionsIterator = existingQuestion.getOptions().iterator();
        while (existingOptionsIterator.hasNext()) {
            QuestionOption existingOption = existingOptionsIterator.next();

            if (requestOptionsMap.containsKey(existingOption.getId())) {
                QuestionOptionRequestDTO optionRequest = requestOptionsMap.get(existingOption.getId());
                existingOption.setText(optionRequest.text());
                existingOption.setIsCorrect(optionRequest.isCorrect());
                requestOptionsMap.remove(existingOption.getId());
            } else {
                existingOptionsIterator.remove();
            }
        }

        addNewOptionsToExistingQuestion(existingQuestion, questionRequest);
    }

    private void addNewOptionsToExistingQuestion(Question existingQuestion, QuestionRequestDTO questionRequest) {
        questionRequest.options().stream()
                .filter(option -> option.id() == null)
                .forEach(optionRequest -> {
                    QuestionOption newOption = questionOptionMapper.toEntity(optionRequest);
                    newOption.setQuestion(existingQuestion);
                    existingQuestion.getOptions().add(newOption);
                });
    }

    public void deleteById(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new SurveyNotFoundException("Encuesta no encontrada con ID: " + id));
        deleteExistingPictureIfPresent(survey);
        surveyRepository.deleteById(id);
    }

    public List<ParticipationResponse> getSurveyParticipants(Long id) {
        List<Participation> participations = participationRepository.findBySurveyId(id);
        return participations.stream()
                .map(participationMapper::toResponse)
                .toList();
    }

}