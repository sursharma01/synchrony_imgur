package com.example.imgur.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private String username;
    private String email;
    private List<ImageResponseDTO> images;
}
