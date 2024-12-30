package com.example.librarymanagement.dtos.requests.action;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReturnBookRequest {

    String isbn;
}
