package com.example.librarymanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    Long paymentId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "loan_id")
//    Loan loan;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    User user;
//
//    @Column(name = "amount")
//    BigDecimal amount;
//
//    @Column(name = "type")
//    String type;
//
//    @Column(name = "status")
//    String status;
//
//    @Column(name = "transaction_ref")
//    String transactionRef;
//
//    @Column(name = "created_at")
//    LocalDateTime createdAt;
//
//    @Column(name = "paid_at")
//    LocalDateTime paidAt;
//
//    @PrePersist
//    void prePersist() {
//        this.createdAt = LocalDateTime.now();
//    }
}
