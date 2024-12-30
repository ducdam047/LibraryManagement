package com.example.librarymanagement.configuration;

import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.UserRole;
import com.example.librarymanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InitAdminConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(userRepository.findByEmail("admin@gmail.com").isEmpty() && userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .email("admin@gmail.com")
                        .username("admin")
                        .password(passwordEncoder.encode("220903"))
                        .status("MANAGER")
                        .role(UserRole.ADMIN.name())
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
