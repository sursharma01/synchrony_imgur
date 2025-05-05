package com.example.imgur.repository;

import com.example.imgur.persistence.Image;
import com.example.imgur.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUser(User user);
}