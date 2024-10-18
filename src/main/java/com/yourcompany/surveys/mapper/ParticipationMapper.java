package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.entity.Participation;
import org.springframework.stereotype.Component;

@Component
public class ParticipationMapper {
    public ParticipationResponse toResponse(Participation participation) {
        return new ParticipationResponse(
                participation.getId(),
                participation.getUser().getId(),
                participation.getUser().getName(),
                participation.getUser().getProfilePictureUrl(),
                participation.getSurvey().getId(),
                participation.getSurvey().getTitle(),
                participation.getParticipatedDate()
        );
    }

}
