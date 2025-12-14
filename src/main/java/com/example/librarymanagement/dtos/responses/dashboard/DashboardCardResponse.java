package com.example.librarymanagement.dtos.responses.dashboard;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardCardResponse {

    long totalBooks;
    long availableBooks;
    long borrowedBooks;
    long totalUsers;
    long borrowingUsers;
    long bannedUsers;
    List overdueRecords;
}
