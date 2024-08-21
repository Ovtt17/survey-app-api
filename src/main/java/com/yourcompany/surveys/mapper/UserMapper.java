package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.UserResponse;
import com.yourcompany.surveys.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public User toEntity(Long id) {
        return User.builder()
                .id(id)
                .build();
    }
}
