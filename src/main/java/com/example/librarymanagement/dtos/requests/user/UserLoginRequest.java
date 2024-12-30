package com.example.librarymanagement.dtos.requests.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginRequest {

    String username;
    String password;
}
