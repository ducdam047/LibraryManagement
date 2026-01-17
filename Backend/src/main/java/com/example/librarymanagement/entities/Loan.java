package com.example.librarymanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    int loanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "book_id", nullable = true)
    Book book;

    @Column(name = "title", nullable = true)
    String title;

    @Column(name = "borrow_day")
    LocalDate borrowDay;

    @Column(name = "borrow_days")
    Integer borrowDays;

    @Column(name = "due_day")
    LocalDate dueDay;

    @Column(name = "returned_day")
    LocalDate returnedDay;

    @Column(name = "borrow_status")
    String borrowStatus;

    @Column(name = "deposit_required")
    BigDecimal depositRequired;

    @Column(name = "deposit_paid")
    Boolean depositPaid;

    @Column(name = "borrow_fee")
    BigDecimal borrowFee;

    @Column(name = "borrow_fee_paid")
    Boolean borrowFeePaid;

    @Column(name = "total_penalty")
    BigDecimal totalPenalty;

    @Column(name = "extend_count")
    int extendCount;
}
