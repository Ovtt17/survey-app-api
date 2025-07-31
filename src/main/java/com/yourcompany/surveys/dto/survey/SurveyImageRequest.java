package com.yourcompany.surveys.dto.survey;

import com.yourcompany.surveys.enums.ImageType;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record SurveyImageRequest (
        MultipartFile picture,
        Long surveyId,
        String username,
        ImageType imageType
) {
}
