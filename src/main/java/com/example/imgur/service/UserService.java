package com.example.imgur.service;

import com.example.imgur.exception.AuthenticationException;
import com.example.imgur.exception.UserNotFoundException;
import com.example.imgur.model.ImageResponseDTO;
import com.example.imgur.model.UserResponseDTO;
import com.example.imgur.persistence.Image;
import com.example.imgur.persistence.User;
import com.example.imgur.repository.ImageRepository;
import com.example.imgur.repository.UserRepository;
import com.example.imgur.util.Convertorutil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducerService kafkaProducerService;
    private final ImgurService imgurService;
    private final Convertorutil convertorutil;

    // Register a new user
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Authenticate user
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        return user;
    }

    // Upload image to Imgur and associate with user
    private Image uploadImage(String username, String password, MultipartFile imageData) {
        User user = authenticate(username, password);

        ResponseEntity<String> imgurResponse = imgurService.uploadImage(imageData);
        Image image = new Image();
        if(imgurResponse.getStatusCode().equals(HttpStatus.CREATED)) {
            String imgurLink = imgurResponse.getBody();
            image.setImageUrl(imgurLink);
            image.setUser(user);
            imageRepository.save(image);

            kafkaProducerService.sendMessage(username, imgurLink);
        }else {
            log.info("Got error from Imgur service while uploading image");
        }

        return image;
    }

    public List<ImageResponseDTO> viewImages(String username, String password) {
        User user = authenticate(username, password);
        List<Image> images = imageRepository.findByUser(user);
        return convertorutil.mapImageToDTO(images);
    }

    // Delete an image
    public void deleteImage(String username, String password, Long imageId) {
        User user = authenticate(username, password);
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new UserNotFoundException("Image not found with id: " + imageId));

        if (!image.getUser().getId().equals(user.getId())) {
            throw new AuthenticationException("You are not authorized to delete this image.");
        }
        imgurService.deleteImage(image.getImageUrl());
        imageRepository.delete(image);
    }

    public UserResponseDTO getUserProfile(String username, String password) {
        User user = authenticate(username, password);
        List<Image> images = imageRepository.findByUser(user);
        user.setImages(images);
        return convertorutil.mapUserToDTO(user);
    }

    public void removeImageFromUser(String username, String imageId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        Image image = imageRepository.findById(Long.parseLong(imageId))
                .orElseThrow(() -> new UserNotFoundException("Image not found with id: " + imageId));

        if (!image.getUser().getId().equals(user.getId())) {
            throw new AuthenticationException("You are not authorized to delete this image.");
        }

        imageRepository.delete(image);
    }

    public List<ImageResponseDTO> addImageToUser(String username, MultipartFile imageUrl) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        Image image = new Image();
        image.setImageUrl(imageUrl.getName());
        image.setUser(user);
        //uploading image too
        uploadImage(username, user.getPassword(), imageUrl);
        imageRepository.save(image);
        return convertorutil.mapImageToDTO(List.of(image));

    }
}
