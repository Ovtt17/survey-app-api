package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.RatingRequestDTO;
import com.yourcompany.surveys.entity.Rating;
import com.yourcompany.surveys.mapper.RatingMapper;
import com.yourcompany.surveys.repository.RatingRepository;
import com.yourcompany.surveys.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final UserRepository userRepository;

    public void createRating(@Valid RatingRequestDTO ratingRequest, Principal principal) {
        Rating rating = ratingMapper.toEntity(ratingRequest);
        rating.setUser(userRepository.findByEmail(principal.getName()).orElseThrow());
        ratingRepository.save(rating);
    }
}
