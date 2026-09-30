package com.exam.controller;

import com.exam.dto.UserRegisterDto;
import com.exam.dto.UserResponseDto;
import com.exam.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register a new user (Student or Teacher)
    // POST http://localhost:8081/api/v1/users/register
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRegisterDto registerDto) {
        UserResponseDto createdUser = userService.registerUser(registerDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED); // 201 Created
    }

    // Get a user by ID
    // GET http://localhost:8081/api/v1/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user); // 200 OK
    }

    // Get a user by Username
    // GET http://localhost:8081/api/v1/users/username/{username}
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        UserResponseDto user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user); // 200 OK
    }

    // Get all registered users
    // GET http://localhost:8081/api/v1/users
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users); // 200 OK
    }
}