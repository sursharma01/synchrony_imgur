package com.example.imgur.model;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String email;
}
