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
public class RecordModel {

    String fullName;
    String title;
    LocalDate borrowDay;
    LocalDate dueDay;
    String status;
    int extendCount;
}
