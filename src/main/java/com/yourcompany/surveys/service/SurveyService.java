package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.dto.survey.*;
import com.yourcompany.surveys.entity.*;
import com.yourcompany.surveys.enums.ImageType;
import com.yourcompany.surveys.handler.exception.ImageDeletionException;
import com.yourcompany.surveys.handler.exception.ImageNoContentException;
import com.yourcompany.surveys.handler.exception.SurveyNotFoundException;
import com.yourcompany.surveys.handler.exception.UnauthorizedException;
import com.yourcompany.surveys.mapper.ParticipationMapper;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final ParticipationRepository participationRepository;
    private final ParticipationMapper participationMapper;
    private final UserService userService;
    private final SurveyImageService surveyImageService;

    public SurveyPagedResponse getAllSurveys(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Survey> surveys = surveyRepository.findAll(pageable);
        if (surveys.isEmpty()) {
            return null;
        }
        return surveyMapper.toPagedResponse(surveys);
    }

    public Survey findByIdOrThrow(Long id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new SurveyNotFoundException("No se encontró la encuesta."));
    }

    public SurveyResponse findById(Long id) {
        Survey survey = findByIdOrThrow(id);
        return surveyMapper.toResponse(survey);
    }

    public SurveySubmissionResponse findByIdForSubmission(Long id) {
        Survey survey = findByIdOrThrow(id);
        return surveyMapper.toSubmissionResponse(survey);
    }

    public SurveySubmissionResponse findByIdForOwner(Long id) {
        User user = userService.getAuthenticatedUser();
        Survey survey = surveyRepository.findByIdAndCreatedBy(id, user);
        if (survey == null) {
            throw new SurveyNotFoundException("No se encontró la encuesta.");
        }
        return surveyMapper.toSubmissionResponse(survey);
    }

    public List<SurveyResponse> getByUserForReport() {
        User user = userService.getAuthenticatedUser();
        List<Survey> surveys = surveyRepository.findByCreatedBy(user);
        return surveys.stream()
                .map(surveyMapper::toResponse)
                .toList();
    }

    public SurveyPagedResponse getByUsernameWithPaging(
            String username,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Survey> surveys = surveyRepository.findByCreatedByUsername(username, pageable);
        if (surveys.isEmpty()) {
            return null;
        }
        return surveyMapper.toPagedResponse(surveys);
    }

    @Transactional
    public Survey save(Survey survey) {
        return surveyRepository.save(survey);
    }

    @Transactional
    public String save(SurveyRequestDTO surveyRequest, MultipartFile picture) {
        User user = userService.getAuthenticatedUser();
        Survey survey = surveyMapper.toEntity(surveyRequest);
        processSurveyPictureIfPresent(picture, survey, user);
        Survey savedSurvey = surveyRepository.save(survey);
        return savedSurvey.getTitle();
    }

    @Transactional
    public String updateSurveyPicture(Long surveyId, MultipartFile newPicture) {
        User user = userService.getAuthenticatedUser();
        Survey survey = findByIdOrThrow(surveyId);
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

    public String deleteSurveyPicture(Long surveyId) {
        User user = userService.getAuthenticatedUser();
        Survey survey = findByIdOrThrow(surveyId);
        validateSurveyOwnership(survey, user);

        String pictureUrl = survey.getPictureUrl();
        if (pictureUrl == null || pictureUrl.isEmpty()) {
            throw new ImageNoContentException("La encuesta no tiene foto.");
        }

        deleteExistingPicture(pictureUrl);
        survey.setPictureUrl(null);
        surveyRepository.save(survey);
        return "Foto de la encuesta eliminada correctamente de la encuesta ." + surveyId;
    }

    @Transactional
    public Long update(
            Long surveyId,
            SurveyRequestDTO surveyRequest,
            MultipartFile picture
    ) {
        User user = userService.getAuthenticatedUser();
        Survey existingSurvey = findByIdOrThrow(surveyId);
        Survey surveyUpdated = surveyMapper.toEntity(existingSurvey, surveyRequest);

        validateSurveyOwnership(surveyUpdated, user);
        deleteExistingPictureIfPresent(existingSurvey);
        processSurveyPictureIfPresent(picture, surveyUpdated, user);

        Survey surveySaved = surveyRepository.save(surveyUpdated);

        return surveySaved.getId();
    }

    private void validateSurveyOwnership(Survey survey, User user) {
        User creator = survey.getCreatedBy();
        if (!creator.getId().equals(user.getId())) {
            throw new UnauthorizedException("No tienes permiso para actualizar esta encuesta.");
        }
    }

    @Transactional
    public void deleteById(Long id) {
        Survey survey = findByIdOrThrow(id);
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