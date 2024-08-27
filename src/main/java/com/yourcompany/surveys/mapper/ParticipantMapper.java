package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.participant.ParticipantResponse;
import com.yourcompany.surveys.entity.Participant;
import org.springframework.stereotype.Component;

@Component
public class ParticipantMapper {
    public ParticipantResponse toResponse(Participant participant) {
        return new ParticipantResponse(
                participant.getId(),
                participant.getUser().getId(),
                participant.getUser().getName(),
                participant.getSurvey().getId(),
                participant.getSurvey().getTitle(),
                participant.getParticipatedDate()
        );
    }

}
