package com.example.librarymanagement.dtos.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardModel {

    long totalBooks;
    long availableBooks;
    long borrowedBooks;
    long totalUsers;
    long borrowingUsers;
    long bannedUsers;
    List overdueRecords;
}
