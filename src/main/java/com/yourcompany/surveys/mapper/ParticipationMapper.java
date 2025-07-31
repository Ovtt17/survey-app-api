package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.entity.Participation;
import org.springframework.stereotype.Component;

@Component
public class ParticipationMapper {
    public ParticipationResponse toResponse(Participation participation) {
        return new ParticipationResponse(
                participation.getId(),
                participation.getCreatedBy().getId(),
                participation.getCreatedBy().getName(),
                participation.getCreatedBy().getProfilePictureUrl(),
                participation.getSurvey().getId(),
                participation.getSurvey().getTitle(),
                participation.getCreatedDate()
        );
    }

}
