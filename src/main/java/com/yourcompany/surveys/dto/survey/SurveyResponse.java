package com.yourcompany.surveys.dto.survey;

import com.yourcompany.surveys.dto.question.QuestionResponse;
import com.yourcompany.surveys.dto.user.UserResponse;

import java.util.List;

public record SurveyResponse (
        Long id,
        String title,
        String description,
        UserResponse creator,
        List<QuestionResponse> questions,
        Double averageRating,
        Long ratingCount
){
}
