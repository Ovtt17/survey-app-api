package com.yourcompany.surveys.dto.user;

public record UserResponse(
        String username,
        String firstName,
        String fullName,
        String lastName,
        String profilePictureUrl
) {
}
