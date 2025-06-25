package com.yourcompany.surveys.dto.user;

public record OAuthUserInfo(
        String email,
        String givenName,
        String familyName,
        String picture
) {}