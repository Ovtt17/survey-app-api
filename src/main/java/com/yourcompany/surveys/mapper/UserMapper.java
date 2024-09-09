package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.user.UserResponse;
import com.yourcompany.surveys.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getName(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getProfilePictureUrl()
        );
    }

    public User toEntity(String username) {
        return User.builder()
                .username(username)
                .build();
    }
}
