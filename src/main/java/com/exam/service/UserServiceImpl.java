package com.exam.service;

import com.exam.dto.UserRegisterDto;
import com.exam.dto.UserResponseDto;
import com.exam.entity.Role;
import com.exam.entity.User;
import com.exam.exception.BadRequestException;
import com.exam.exception.ResourceNotFoundException;
import com.exam.repository.RoleRepository;
import com.exam.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Injected PasswordEncoder bean
    public UserServiceImpl(UserRepository userRepository, 
                           RoleRepository roleRepository, 
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponseDto registerUser(UserRegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("Username '" + dto.getUsername() + "' is already taken.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email '" + dto.getEmail() + "' is already registered.");
        }

        String roleName = (dto.getRole() != null && !dto.getRole().trim().isEmpty())
                ? dto.getRole().toUpperCase()
                : "ROLE_STUDENT";

        Role assignedRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // Hash password before saving to PostgreSQL
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.getRoles().add(assignedRole);

        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return mapToResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private UserResponseDto mapToResponseDto(User user) {
        Set<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                roleNames
        );
    }
}