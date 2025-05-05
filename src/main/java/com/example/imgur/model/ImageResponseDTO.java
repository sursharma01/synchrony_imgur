package com.example.imgur.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageResponseDTO {
    private Long id;
    private String url;
}
