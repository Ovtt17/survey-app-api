package com.yourcompany.surveys.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {

    @Value("${imgur.url.upload}")
    private String imgur_url;

    @Value("${imgur.access_token}")
    private String accessToken;

    private static final long MAX_IMAGE_SIZE_MB = 5;
    private static final long MAX_IMAGE_SIZE_BYTES = MAX_IMAGE_SIZE_MB * 1024 * 1024;

    public String uploadImage (MultipartFile image, String username, String imageType) {
        try {
            if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
                return "El tamaño de la imagen excede el límite de 5 MB.";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + accessToken);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });
            body.add("type", "file");
            String imageName = username + "_" + imageType;
            body.add("type", "file");
            body.add("name", imageName);
            body.add("title", imageName);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map<String, Object>> response = restTemplate
                    .exchange(imgur_url,HttpMethod.POST,request,new ParameterizedTypeReference<>() {} );

            Map<String, Object> responseData = response.getBody();
            if (responseData != null) {
                Object data = responseData.get("data");
                if (data instanceof Map) {
                    Object imageLink = ((Map<?, ?>) data).get("link");
                    if (imageLink instanceof String) {
                        return (String) imageLink;
                    }
                }
            }
            return "Error al subir la imagen a Imgur o el enlace no es válido.";
        } catch (IOException e) {
            return "Error al procesar la imagen: " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado: " + e.getMessage();
        }
    }
}