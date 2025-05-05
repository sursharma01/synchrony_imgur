package com.example.imgur.service;

import com.example.imgur.model.UserRequest;
import com.example.imgur.persistence.User;
import com.example.imgur.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "john", "password123", "john@yahoo.com", null, null);
    }

    @Test
    void testRegisterUser() {
        when(userRepository.save(user)).thenReturn(user);
        User savedUser = userService.registerUser(user);
        assertNotNull(savedUser);
    }
}
