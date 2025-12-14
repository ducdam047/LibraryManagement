package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.requests.user.ChangePasswordRequest;
import com.example.librarymanagement.dtos.requests.user.LoginRequest;
import com.example.librarymanagement.dtos.requests.user.SignupRequest;
import com.example.librarymanagement.dtos.requests.user.UpdateRequest;
import com.example.librarymanagement.dtos.responses.ApiResponse;
import com.example.librarymanagement.dtos.responses.AuthenticationResponse;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.services.AuthenticationService;
import com.example.librarymanagement.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ApiResponse<User> signupUser(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.<User>builder()
                .code(201)
                .message("Registered successfully")
                .data(userService.signupUser(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> loginUser(@RequestBody LoginRequest request) {
        var data = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .message("Login successfully")
                .data(data)
                .build();
    }

    @GetMapping("/profile")
    public ApiResponse<User> profileUser() {
        User user = userService.getProfile();
        return ApiResponse.<User>builder()
                .code(200)
                .message("Show profile")
                .data(user)
                .build();
    }

    @PutMapping("/update/{userId}")
    public ApiResponse<User> updateUser(@PathVariable int userId, @RequestBody UpdateRequest request) {
        User user = userService.updateUser(userId, request);
        return ApiResponse.<User>builder()
                .code(200)
                .message("User updated successfully")
                .data(user)
                .build();
    }

    @PutMapping("/changePassword/{userId}")
    public ApiResponse<User> changePassword(@PathVariable int userId, @RequestBody ChangePasswordRequest request) {
        User user = userService.changePassword(userId, request);
        return ApiResponse.<User>builder()
                .code(200)
                .message("User change password successfully")
                .data(user)
                .build();
    }

    @GetMapping("/reloadUser")
    public ApiResponse<User> reloadUser() {
        User user = userService.reloadUser();
        return ApiResponse.<User>builder()
                .code(200)
                .message("Reload user successfully")
                .data(user)
                .build();
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }
}
