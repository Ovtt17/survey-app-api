package com.yourcompany.surveys.dto.survey;

import com.yourcompany.surveys.entity.ImageType;
import org.springframework.web.multipart.MultipartFile;

public record SurveyImageRequest (
        MultipartFile image,
        Long surveyId,
        String username,
        ImageType imageType
) {
}
