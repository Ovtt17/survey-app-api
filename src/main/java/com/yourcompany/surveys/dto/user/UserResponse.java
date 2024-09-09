package com.yourcompany.surveys.dto.user;

public record UserResponse(
        String username,
        String firstName,
        String lastName,
        String fullName,
        String profilePictureUrl
) {
}
