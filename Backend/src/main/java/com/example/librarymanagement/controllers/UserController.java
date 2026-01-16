package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.requests.user.ChangePasswordRequest;
import com.example.librarymanagement.dtos.requests.user.LoginRequest;
import com.example.librarymanagement.dtos.requests.user.SignupRequest;
import com.example.librarymanagement.dtos.requests.user.UpdateRequest;
import com.example.librarymanagement.common.ApiResponse;
import com.example.librarymanagement.dtos.responses.authentication.AuthenticationResponse;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.services.AuthenticationService;
import com.example.librarymanagement.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signupUser(@Valid @RequestBody SignupRequest request) {
        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                .code(201)
                .message("Registered successfully")
                .data(userService.signupUser(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> loginUser(@RequestBody LoginRequest request) {
        AuthenticationResponse data = authenticationService.authenticate(request);
        ApiResponse<AuthenticationResponse> apiResponse = ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .message("Login successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping()
    public ApiResponse<List<User>> getUsers() {
        return ApiResponse.<List<User>>builder()
                .code(200)
                .message("List users")
                .data(userService.getUsers())
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

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateUser(@RequestBody UpdateRequest request) {
        User user = userService.updateUser(request);
        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                .code(200)
                .message("User updated successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<User>> changePassword(@RequestBody ChangePasswordRequest request) {
        User user = userService.changePassword(request);
        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                .code(200)
                .message("User change password successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(apiResponse);
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
}
