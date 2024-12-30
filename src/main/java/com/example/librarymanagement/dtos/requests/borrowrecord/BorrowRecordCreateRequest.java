package com.example.librarymanagement.dtos.requests.borrowrecord;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRecordCreateRequest {

    String title;
    int borrowDays;
}
