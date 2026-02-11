package com.example.librarymanagement.dtos.requests.action;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowBookRequest {

    @NotBlank(message = "Book title must not be blank")
    String title;

    @Min(value = 1, message = "Borrow days must be at least 1")
    int borrowDays;
}
