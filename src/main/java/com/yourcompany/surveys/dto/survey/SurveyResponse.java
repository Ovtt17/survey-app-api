package com.yourcompany.surveys.dto.survey;

public record SurveyResponse (
        Long id,
        String title,
        String description,
        String creatorFullName,
        String creatorUsername,
        String creatorProfilePicture,
        Double averageRating,
        Long ratingCount,
        String pictureUrl
) {
}
