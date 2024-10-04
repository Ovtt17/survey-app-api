package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.survey.SurveyImageRequest;
import com.yourcompany.surveys.dto.survey.SurveyResponse;
import com.yourcompany.surveys.handler.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyImageService {
    private final ImageService imageService;
    private final SurveyService surveyService;

    public String uploadSurveyPicture(
            SurveyImageRequest request
    ) {
        try {
            SurveyResponse survey = surveyService.findById(request.surveyId());
            String currentSurveyPictureUrl = survey.pictureUrl();

            if (currentSurveyPictureUrl != null) {
                imageService.deleteImage(currentSurveyPictureUrl);
            }
            String surveyPictureName = "survey_" + request.surveyId() + "_" + request.username() + "_" + request.imageType().getType();
            String newSurveyPictureUrl = imageService.uploadImage(request.image(), surveyPictureName);
            surveyService.updateSurveyPicture(request.surveyId(), newSurveyPictureUrl);
            return newSurveyPictureUrl;
        } catch (Exception e) {
            throw new ImageUploadException("Error al subir la foto de la encuesta: " + e.getMessage());
        }
    }
}
