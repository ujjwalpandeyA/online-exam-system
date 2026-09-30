package com.exam.repository;

import com.exam.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Generates: SELECT * FROM roles WHERE name = ?
    Optional<Role> findByName(String name);
}