package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findByStatus(String status);
    List<User> findByStatus(UserStatus status);
    long countByStatus(UserStatus status);
    long countByBanUtilAfter(LocalDate now);
}
