package com.yourcompany.surveys.dto.survey;

import com.yourcompany.surveys.entity.ImageType;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record SurveyImageRequest (
        MultipartFile image,
        Long surveyId,
        String username,
        ImageType imageType
) {
}
