package com.example.librarymanagement.dtos.requests.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignupRequest {

    @Email(message = " The email is not in the correct format")
    @NotBlank(message = "Email cannot be left blank")
    String email;
    @NotBlank(message = "Full name cannot be left blank")
    String fullName;
    @NotBlank(message = "Username cannot be left blank")
    String username;
    @NotBlank(message = "Password cannot be left blank")
    String password;
    @NotBlank(message = "Confirm password cannot be left blank")
    String confirmPassword;
    String phoneNumber;
    String cid;
    String address;
}
