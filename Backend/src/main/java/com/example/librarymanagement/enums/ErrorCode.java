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
    BOOK_HAS_NO_PDF(1016, "Book has no PDF file", HttpStatus.BAD_REQUEST),
    PDF_FILE_NOT_FOUND(1026, "PDF file not found", HttpStatus.NOT_FOUND),
    PDF_PREVIEW_FAILED(1036, "Cannot preview PDF", HttpStatus.INTERNAL_SERVER_ERROR),
    BOOK_OUT_OF_STOCK(10046, "Book out of stock", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1007, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_ID_EXISTS(1017, "Category ID already exists", HttpStatus.BAD_REQUEST),
    CATEGORY_NAME_EXISTS(1027, "Category name already exists", HttpStatus.BAD_REQUEST),
    PUBLISHER_ID_EXISTS(1037, "Publisher ID already exists", HttpStatus.BAD_REQUEST),
    PUBLISHER_NAME_EXISTS(1047, "Publisher name already exists", HttpStatus.BAD_REQUEST),
    PUBLISHER_NOT_FOUND(1007, "Publisher not found", HttpStatus.NOT_FOUND),
    LOAN_NOT_FOUND(1008, "Loan not found", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_FOUND(1018, "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_COMPLETED(1028, "Payment has not been completed", HttpStatus.CONFLICT),
    WISHLIST_NOT_FOUND(1009, "Wishlist not found", HttpStatus.NOT_FOUND),
    WISHLIST_EXISTED(1010, "This book was added", HttpStatus.CONFLICT),
    BOOK_EVALUATED(1011, "This book was evaluated", HttpStatus.CONFLICT),
    READING_NOT_FOUND(1012, "Reading not found", HttpStatus.NOT_FOUND),
    BOOK_BORROWED(2001, "You are borrowing this book", HttpStatus.CONFLICT),
    BOOK_REQUESTED(2002, "You are requesting this book", HttpStatus.CONFLICT),
    ACCOUNT_BANNED(3002, "You are forbidden to borrow book", HttpStatus.FORBIDDEN),
    BORROW_LIMIT_REACHED(3003, "You have reached the maximum borrowing limit", HttpStatus.BAD_REQUEST),
    BORROW_DAYS_EXCEEDED(3005, "The borrowing period must not exceed 7 days", HttpStatus.BAD_REQUEST),
    EXTEND_DEADLINE_EXPIRED(3004, "The extension deadline has expired", HttpStatus.BAD_REQUEST),
    EXTEND_LIMIT_EXCEEDED(3006, "You have reached the maximum number of extensions", HttpStatus.BAD_REQUEST),
    EXTEND_DAY_EXCEEDED(3007, "The extending period must not exceed 3 days", HttpStatus.BAD_REQUEST),
    NOT_BORROWED(3008, "You have not borrowed this book before", HttpStatus.BAD_REQUEST),
    NOT_ELIGIBLE_TO_EVALUATE(3009, "You can only evaluate borrowed or returned books", HttpStatus.BAD_REQUEST),
    INVALID_EXTEND_DAY(3010, "The number of extension days must be greater than 0", HttpStatus.BAD_REQUEST),
    INVALID_SIGNATURE(4001, "Invalid payment signature", HttpStatus.UNAUTHORIZED),
    INVALID_LOAN_STATE(4002, "Loan is not in a valid state for this operation", HttpStatus.CONFLICT)
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
