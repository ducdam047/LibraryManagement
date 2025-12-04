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
    BOOK_EVALUATED(1011, "This book was evaluated", HttpStatus.CONFLICT),
    READING_NOT_FOUND(1012, "Reading not found", HttpStatus.NOT_FOUND),
    BOOK_BORROWED(2001, "You are borrowing this book", HttpStatus.CONFLICT),
    ACCOUNT_LOCKED(3002, "Your account has been locked", HttpStatus.FORBIDDEN),
    BORROW_LIMIT_REACHED(3003, "You have reached the maximum borrowing limit", HttpStatus.BAD_REQUEST),
    BORROW_DAYS_EXCEEDED(3005, "The borrowing period must not exceed 7 days", HttpStatus.BAD_REQUEST),
    EXTEND_DEADLINE_EXPIRED(3004, "The extension deadline has expired", HttpStatus.BAD_REQUEST),
    EXTEND_LIMIT_EXCEEDED(3006, "You have reached the maximum number of extensions", HttpStatus.BAD_REQUEST),
    EXTEND_DAY_EXCEEDED(3007, "The extending period must not exceed 3 days", HttpStatus.BAD_REQUEST),
    NOT_BORROWED(3008, "You have not borrowed this book before", HttpStatus.BAD_REQUEST),
    NOT_ELIGIBLE_TO_EVALUATE(3009, "You can only evaluate borrowed or returned books", HttpStatus.BAD_REQUEST),
    INVALID_EXTEND_DAY(3010, "The number of extension days must be greater than 0", HttpStatus.BAD_REQUEST)
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
