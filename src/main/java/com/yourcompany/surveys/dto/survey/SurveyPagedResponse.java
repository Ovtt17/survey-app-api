package com.yourcompany.surveys.dto.survey;

import java.util.List;

public record SurveyPagedResponse (
        List<SurveyResponse> surveys,
        int page,
        int totalPages
) {
}
