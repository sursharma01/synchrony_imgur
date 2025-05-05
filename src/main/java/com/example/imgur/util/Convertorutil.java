package com.example.imgur.util;

import com.example.imgur.model.ImageResponseDTO;
import com.example.imgur.model.UserRequest;
import com.example.imgur.model.UserResponseDTO;
import com.example.imgur.persistence.Image;
import com.example.imgur.persistence.User;
import com.example.imgur.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class Convertorutil {
    private final UserRepository userRepository;
    public User convertToUserEntity(UserRequest user) {
        User userEntity = new User();
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(user.getPassword());
        userEntity.setEmail(user.getEmail());
        userEntity.setCreatedAt(Date.valueOf(java.time.LocalDate.now()));
        return userEntity;
    }

    public String findPasswordFromUserName(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getPassword).orElse(null);
    }

    public UserResponseDTO mapUserToDTO(User user) {
        List<ImageResponseDTO> images = user.getImages().stream()
                .map(img -> new ImageResponseDTO(img.getId(), img.getImageUrl()))
                .toList();

        return new UserResponseDTO(user.getUsername(), user.getEmail(), images);
    }

    public List<ImageResponseDTO> mapImageToDTO(List<Image> images) {
        return images.stream().map(image -> new ImageResponseDTO(image.getId(), image.getImageUrl())).toList();
    }
}
