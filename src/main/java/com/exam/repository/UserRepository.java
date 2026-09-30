package com.exam.repository;

import com.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used during authentication and login
    Optional<User> findByUsername(String username);

    // Used to find student/teacher by email
    Optional<User> findByEmail(String email);

    // Used during registration validation to prevent duplicates
    Boolean existsByUsername(String username);

    // Used during registration validation to prevent duplicate email accounts
    Boolean existsByEmail(String email);
}