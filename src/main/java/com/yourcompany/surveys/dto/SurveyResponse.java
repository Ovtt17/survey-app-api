package com.yourcompany.surveys.dto;

import java.util.List;

public record SurveyResponse (
        Long id,
        String title,
        String description,
        UserResponse creator,
        List<QuestionResponse> questions
){
}
