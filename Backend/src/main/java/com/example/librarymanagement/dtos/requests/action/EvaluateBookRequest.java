package com.example.librarymanagement.dtos.requests.action;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EvaluateBookRequest {

    int bookId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    int rating;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    String comment;
}
