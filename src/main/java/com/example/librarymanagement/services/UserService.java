package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.requests.user.UserSignupRequest;
import com.example.librarymanagement.dtos.requests.user.UserUpdateRequest;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.enums.UserRole;
import com.example.librarymanagement.enums.UserStatus;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User signupUser(UserSignupRequest request) {
        if(userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        if(!request.getPassword().equals(request.getConfirmPassword()))
            throw new AppException(ErrorCode.PASSWORD_NOT_CONFIRM);

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .status(UserStatus.ACTIVE.name())
                .bookBorrowing(0)
                .role(UserRole.USER.name())
                .build();
        return userRepository.save(user);
    }

    @PostAuthorize("returnObject.email == authentication.name")
    public User getProfile() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }

    @PostAuthorize("returnObject.email == authentication.name")
    public User updateUser(int userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
