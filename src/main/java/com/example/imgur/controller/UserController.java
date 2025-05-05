package com.example.imgur.controller;

import com.example.imgur.exception.RetriableException;
import com.example.imgur.model.ImageResponseDTO;
import com.example.imgur.model.UserRequest;
import com.example.imgur.model.UserResponseDTO;
import com.example.imgur.persistence.User;
import com.example.imgur.service.UserService;
import com.example.imgur.service.ImgurService;
import com.example.imgur.util.Convertorutil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ImgurService imgurService;
    private final Convertorutil convertorutil;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/register")
    @CircuitBreaker(name = "registerUser", fallbackMethod = "registerUserFallback")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRequest userRequest) {
        log.debug("Registering userRequest: {}", userRequest.getUsername());
        User userEntity = convertorutil.convertToUserEntity(userRequest);
        return ResponseEntity.ok(userService.registerUser(userEntity));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{username}/upload-image")
    @CircuitBreaker(name = "imageService", fallbackMethod = "uploadImageFallback")
    public ResponseEntity<List<ImageResponseDTO>> uploadImage(@PathVariable String username,
                                                              @RequestParam("image") MultipartFile image) {
        log.debug("Uploading image for user: {}", username);
        ResponseEntity<String> imageUrl = imgurService.uploadImage(image);
        List<ImageResponseDTO> response = userService.addImageToUser(username, (MultipartFile) imageUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{username}/delete-image/{imageId}")
    @CircuitBreaker(name = "deleteImage", fallbackMethod = "deleteImageFallBack")
    public ResponseEntity<String> deleteImage(@PathVariable String username, @PathVariable String imageId) {
        log.debug("Deleting image for user: {}", username);
        imgurService.deleteImage(imageId);
        userService.removeImageFromUser(username, imageId);
        log.info("Image deleted successfully for user: {} with imageId: {}\"", username, imageId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{username}")
    @CircuitBreaker(name = "getUser", fallbackMethod = "getUserFallBack")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String username) {
        log.debug("Fetching user: {}", username);
        String password = convertorutil.findPasswordFromUserName(username);
        if (password == null) {
            log.error("User not found: {}", username);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userService.getUserProfile(username, password));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/images/{username}")
    @CircuitBreaker(name = "getImages", fallbackMethod = "getImagesFallBack")
    public ResponseEntity<List<ImageResponseDTO>> getImages(@PathVariable String username) {
        log.debug("Fetching user: {}", username);
        String password = convertorutil.findPasswordFromUserName(username);
        if (password == null) {
            log.error("User not found: {}", username);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userService.viewImages(username, password));
    }


    public ResponseEntity<?> uploadImageFallback(MultipartFile file, Throwable throwable) {
        log.error("Fallback triggered for uploadImage due to: {}", throwable.getMessage());
       log.info("Image upload failed. Please try again later.");
        throw new RetriableException(throwable.getMessage());
    }

    public ResponseEntity<?> getUserFallBack(String name, Throwable throwable) {
        log.error("Fallback triggered for uploadImage due to: {}", throwable.getMessage());
        log.info("Image upload failed. Please try again later.");
        throw new RetriableException(throwable.getMessage());
    }


    public ResponseEntity<?> deleteImageFallBack(String name, String id, Throwable throwable) {
        log.error("Fallback triggered for uploadImage due to: {}", throwable.getMessage());
        log.info("Image upload failed. Please try again later.");
        throw new RetriableException(throwable.getMessage());
    }
    public ResponseEntity<?> registerUserFallback(UserRequest user, Throwable throwable) {
        log.error("Fallback triggered for uploadImage due to: {}", throwable.getMessage());
        log.info("Image upload failed. Please try again later.");
        throw new RetriableException(throwable.getMessage());
    }
    public ResponseEntity<?> getImagesFallBack(UserRequest user, Throwable throwable) {
        log.error("Fallback triggered for uploadImage due to: {}", throwable.getMessage());
        log.info("Image upload failed. Please try again later.");
        throw new RetriableException(throwable.getMessage());
    }
}