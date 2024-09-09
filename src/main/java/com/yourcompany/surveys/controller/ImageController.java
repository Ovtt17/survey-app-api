package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Tag(name = "Images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public String getImages(
            @RequestBody MultipartFile image
    ) throws IOException {
        return imageService.uploadImage(image);
    }
}
