package com.example.librarymanagement.services;

import com.example.librarymanagement.entities.Loan;
import com.example.librarymanagement.entities.Payment;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.enums.LoanStatus;
import com.example.librarymanagement.enums.PaymentStatus;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.LoanRepository;
import com.example.librarymanagement.repositories.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;
    private final VnPayService vnpayService;

    public void confirmPayment(Map<String, String> params) {
        if(!vnpayService.verifySecureHash(params))
            throw new AppException(ErrorCode.INVALID_SIGNATURE);

        String txnRef = params.get("vnp_TxnRef");

        Payment payment = paymentRepository.findByTransactionRef(txnRef)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        if(PaymentStatus.SUCCESS.name().equals(payment.getStatus()))
            return;

        String responseCode = params.get("vnp_ResponseCode");
        if("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.SUCCESS.name());
            payment.setPaidAt(LocalDateTime.now());

            Loan loan = payment.getLoan();
            loan.setBorrowStatus(LoanStatus.PAID.name());
        } else {
            payment.setStatus(PaymentStatus.FAILED.name());
        }
    }
}
