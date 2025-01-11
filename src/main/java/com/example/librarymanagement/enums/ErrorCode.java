package com.example.librarymanagement.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    USER_EXISTED(1001, "User already exists!", HttpStatus.CONFLICT),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1003, "User not authenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1004, "You do not have permission", HttpStatus.FORBIDDEN),
    PASSWORD_NOT_CONFIRM(1005, "Password does not match confirmation", HttpStatus.BAD_REQUEST),
    BOOK_NOT_FOUND(1006, "Book not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1007, "Category not found", HttpStatus.NOT_FOUND),
    PUBLISHER_NOT_FOUND(1007, "Publisher not found", HttpStatus.NOT_FOUND),
    BORROW_RECORD_NOT_FOUND(1008, "Borrow record not found", HttpStatus.NOT_FOUND),
    WISHLIST_NOT_FOUND(1009, "Wishlist not found", HttpStatus.NOT_FOUND),
    WISHLIST_EXISTED(1010, "This book was added", HttpStatus.CONFLICT),
    BOOK_EVALUATED(1011, "This book was evaluated", HttpStatus.CONFLICT)
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
