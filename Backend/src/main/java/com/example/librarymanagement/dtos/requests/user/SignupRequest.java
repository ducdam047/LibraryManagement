package com.example.librarymanagement.dtos.requests.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignupRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is not in the correct format")
    String email;

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 6, max = 50, message = "Full name length is invalid")
    String fullName;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, max = 20, message = "Username length must be between 4 and 20")
    String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password;

    @NotBlank(message = "Confirm password cannot be blank")
    String confirmPassword;

    @Pattern(regexp = "^(0[0-9]{9})?$", message = "Phone number is invalid")
    String phoneNumber;

    @Size(min = 9, max = 12, message = "CID length is invalid")
    String cid;

    @Size(max = 255, message = "Address is too long")
    String address;
}
