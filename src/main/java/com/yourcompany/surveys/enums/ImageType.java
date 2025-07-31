package com.yourcompany.surveys.enums;

import lombok.Getter;

@Getter
public enum ImageType {
    PROFILE_PICTURE("profile_picture"),
    SURVEY_PICTURE("survey_picture");

    private final String type;

    ImageType(String type) {
        this.type = type;
    }
}
