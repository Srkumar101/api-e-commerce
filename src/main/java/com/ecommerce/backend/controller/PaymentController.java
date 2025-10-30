package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Payment;
import com.ecommerce.backend.payload.PaymentRequest;
import com.ecommerce.backend.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    //@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> pay(@RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.processPayment(request);
            return ResponseEntity.ok(payment);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
