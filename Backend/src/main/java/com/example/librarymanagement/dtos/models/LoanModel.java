package com.example.librarymanagement.dtos.models;

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
public class LoanModel {

    int loanId;
    String fullName;
    Integer bookId;
    String title;
    String author;
    String imageUrl;
    LocalDate borrowDay;
    int borrowDays;
    LocalDate dueDay;
    LocalDate returnedDay;
    String borrowStatus;
    int extendCount;
}
