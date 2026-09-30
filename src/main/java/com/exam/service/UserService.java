package com.exam.service;

import com.exam.dto.UserRegisterDto;
import com.exam.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto registerUser(UserRegisterDto registerDto);

    UserResponseDto getUserById(Long userId);

    UserResponseDto getUserByUsername(String username);

    List<UserResponseDto> getAllUsers();
}