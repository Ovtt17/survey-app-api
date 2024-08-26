package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.user.UserResponse;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.mapper.UserMapper;
import com.yourcompany.surveys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getUser(Principal principal) {
        String username = principal.getName();
        Optional<User> userOptional = userRepository.findByEmail(username);
        User user = userOptional.orElseThrow();
        return userMapper.toUserResponse(user);
    }
}
