package com.example.librarymanagement.dtos.models;

import com.example.librarymanagement.enums.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserModel {

    String fullName;
    UserStatus status;
    LocalDate banUtil;
    int bookBorrowing;
}
