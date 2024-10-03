package com.yourcompany.surveys.service;

import com.yourcompany.surveys.entity.ImageType;
import com.yourcompany.surveys.handler.exception.ImageDeletionException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SurveyImageService {
    private final ImageService imageService;
    private final SurveyService surveyService;

    public String uploadSurveyPicture(
            MultipartFile image,
            Long surveyId,
            String username,
            ImageType imageType
    ) {
        String surveyPictureName = "survey_" + surveyId + "_" + username + "_" + imageType.getType();
        return imageService.uploadImage(image, surveyPictureName);
    }

    public ResponseEntity<String> deleteSurveyPicture(Long surveyId, String username) {
        try {
            String surveyPictureUrl = surveyService.getSurveyPictureUrl(surveyId, username);
            if (surveyPictureUrl == null) {
                return ResponseEntity.noContent().build();
            }
            boolean deleted = imageService.deleteImage(surveyPictureUrl);
            if (deleted) {
                return ResponseEntity.ok("Foto de la encuesta eliminada correctamente.");
            } else {
                return ResponseEntity.badRequest().body("Error al eliminar la foto de la encuesta.");
            }
        } catch (ImageDeletionException e) {
            return ResponseEntity.badRequest().body("Error al eliminar la foto de la encuesta: " + e.getMessage());
        }
    }
}
