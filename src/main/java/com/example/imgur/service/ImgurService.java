package com.example.imgur.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class ImgurService {

    @Value("${imgur.client-id}")
    private String clientId;

    private final RestTemplate restTemplate;

    public ResponseEntity<String> uploadImage(MultipartFile imageBytes) {
        String imgurUrl = "https://api.imgur.com/3/image";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + clientId);
        HttpEntity<MultipartFile> entity = new HttpEntity<>(imageBytes, headers);
        return restTemplate.exchange(imgurUrl, HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<String> deleteImage(String imageId) {
        String imgurUrl = "https://api.imgur.com/3/image/" + imageId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + clientId);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(imgurUrl, HttpMethod.DELETE, entity, String.class);
    }

}
