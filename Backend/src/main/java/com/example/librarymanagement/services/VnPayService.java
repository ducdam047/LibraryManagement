package com.example.librarymanagement.services;

import com.example.librarymanagement.entities.Payment;
import org.apache.hc.client5.http.utils.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VnPayService {

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.pay-url}")
    private String payUrl;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    public String createPaymentUrl(Payment payment, String clientIp) {
        Map<String, String> params = new HashMap<>();

        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", payment.getAmount().multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", payment.getTransactionRef());
        params.put("vnp_OrderInfo", "Borrow_book_payment");
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", clientIp);
        params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        params.put("vnp_ExpireDate", LocalDateTime.now().plusMinutes(15).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        List<Map.Entry<String, String>> entries = new ArrayList<>(params.entrySet());
        entries.sort(Map.Entry.comparingByKey());

        String hashData = entries.stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        String secureHash = hmacSHA512(hashSecret, hashData);

        String queryString = entries.stream()
                .map(e -> {
                    try {
                        return e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.joining("&"));

        return payUrl + "?" + queryString + "&vnp_SecureHash=" + secureHash;
    }

    public boolean verifySecureHash(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");

        Map<String, String> filtered = params.entrySet().stream()
                .filter(e -> !e.getKey().equals("vnp_SecureHash") && !e.getKey().equals("vnp_SecureHashType"))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        String hashData = filtered.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        String calculatedHash = hmacSHA512(hashSecret, hashData);

        return calculatedHash.equalsIgnoreCase(receivedHash);
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(raw);
        } catch (Exception e) {
            throw new RuntimeException("Cannot generate HMAC", e);
        }
    }
}
