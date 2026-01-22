package com.example.librarymanagement.dtos.requests.vnpay;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VnPayCallbackRequest {

    String vnp_TxnRef;
    String vnp_Amount;
    String vnp_ResponseCode;
    String vnp_TransactionNo;
    String vnp_PayDate;
    String vnp_SecureHash;
}
