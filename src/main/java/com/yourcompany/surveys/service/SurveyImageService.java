package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.survey.SurveyImageRequest;
import com.yourcompany.surveys.handler.exception.ImageDeletionException;
import com.yourcompany.surveys.handler.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyImageService {
    private final ImageService imageService;

    public String uploadSurveyPicture(
            SurveyImageRequest request
    ) {
        try {
            String surveyPictureName = "survey_" + request.surveyId() + "_" + request.username() + "_" + request.imageType().getType();
            return imageService.uploadImage(request.picture(), surveyPictureName);
        } catch (Exception e) {
            throw new ImageUploadException("Error al subir la foto de la encuesta: " + e.getMessage());
        }
    }

    public boolean deleteSurveyPicture(String pictureUrl) {
        try {
            return imageService.deleteImage(pictureUrl);
        } catch (Exception e) {
            throw new ImageDeletionException("Error al eliminar la foto de la encuesta: " + e.getMessage());
        }
    }
}
