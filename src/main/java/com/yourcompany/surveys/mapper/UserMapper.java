package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.user.UserResponse;
import com.yourcompany.surveys.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        String profilePictureUrl = "";
        if (user.getProfilePictureHash() != null) {
            String IMAGE_BASE_URL = "https://imgur.com/";
            profilePictureUrl = IMAGE_BASE_URL + user.getProfilePictureHash();
        }

        return new UserResponse(
                user.getName(),
                user.getFirstName(),
                user.getFullName(),
                user.getLastName(),
                profilePictureUrl
        );
    }

    public User toEntity(String username) {
        return User.builder()
                .username(username)
                .build();
    }
}
