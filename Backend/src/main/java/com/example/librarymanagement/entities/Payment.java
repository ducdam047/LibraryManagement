package com.example.librarymanagement.entities;

import com.example.librarymanagement.enums.PaymentStatus;
import com.example.librarymanagement.enums.PaymentType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    Loan loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "amount")
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    PaymentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    PaymentStatus status;

    @Column(name = "transaction_ref")
    String transactionRef;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "paid_at")
    LocalDateTime paidAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
