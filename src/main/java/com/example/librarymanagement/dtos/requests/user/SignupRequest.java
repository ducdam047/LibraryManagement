package com.example.librarymanagement.dtos.requests.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignupRequest {

    String email;
    String fullName;
    String username;
    String password;
    String confirmPassword;
    String phoneNumber;
    String cid;
    String address;
}
