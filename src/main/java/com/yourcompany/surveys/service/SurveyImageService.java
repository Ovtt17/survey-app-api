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
}
