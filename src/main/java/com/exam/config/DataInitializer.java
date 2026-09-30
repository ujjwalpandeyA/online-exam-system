package com.exam.config;

import com.exam.entity.Role;
import com.exam.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    // Spring automatically injects RoleRepository via Constructor Injection
    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check and insert ROLE_STUDENT
        if (roleRepository.findByName("ROLE_STUDENT").isEmpty()) {
            roleRepository.save(new Role("ROLE_STUDENT"));
            System.out.println(">> Seeding: ROLE_STUDENT created in database.");
        }

        // Check and insert ROLE_TEACHER
        if (roleRepository.findByName("ROLE_TEACHER").isEmpty()) {
            roleRepository.save(new Role("ROLE_TEACHER"));
            System.out.println(">> Seeding: ROLE_TEACHER created in database.");
        }
    }
}