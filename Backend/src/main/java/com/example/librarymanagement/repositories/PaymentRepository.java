package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Loan;
import com.example.librarymanagement.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionRef(String txnRef);
    Optional<Payment> findByLoanAndStatus(Loan loan, String status);
}
