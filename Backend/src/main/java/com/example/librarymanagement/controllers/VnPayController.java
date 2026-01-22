package com.example.librarymanagement.controllers;

import com.example.librarymanagement.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/vnPay")
@RequiredArgsConstructor
public class VnPayController {

    private final PaymentService paymentService;

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam Map<String, String> params) {
        paymentService.confirmPayment(params);
        return ResponseEntity.ok("OK");
    }
}
