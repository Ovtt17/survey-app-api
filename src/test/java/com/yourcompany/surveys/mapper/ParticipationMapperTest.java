package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.entity.Participation;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ParticipationMapperTest {
    private ParticipationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ParticipationMapper();
    }

    @Test
    public void should_map_participation_to_response() {
        User user = User.builder()
                .id(2L)
                .username("John Doe")
                .profilePictureUrl("https://example.com/johndoe.jpg")
                .build();

        Survey survey = Survey.builder()
                .id(1L)
                .title("Customer Satisfaction Survey")
                .build();

        Participation participation = new Participation();
        participation.setId(1L);
        participation.setCreatedBy(user);
        participation.setSurvey(survey);
        participation.setCreatedDate(LocalDateTime.now());

        ParticipationResponse response = mapper.toResponse(participation);

        assertEquals(response.id(), participation.getId());
        assertEquals(response.userId(), user.getId());

        // getName() is used to retrieve the username because of the User class design
        assertEquals(response.username(), user.getName());
        assertEquals(response.profilePictureUrl(), user.getProfilePictureUrl());
        assertEquals(response.surveyId(), survey.getId());
        assertEquals(response.surveyTitle(), survey.getTitle());
        assertNotNull(response.participatedDate());
    }
}