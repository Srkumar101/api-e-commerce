package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.Payment;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.PaymentRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.payload.PaymentRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public Payment processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found."));

        if (order.getPayment() != null) {
            throw new IllegalStateException("Order is already paid.");
        }

        if (!order.getTotalAmount().equals(request.getAmount())) {
            throw new IllegalArgumentException("Amount does not match order total.");
        }

        if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("Order is not eligible for payment.");
        }

        User user = getCurrentUser();
        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Cannot pay for another user's order.");
        }

        Payment payment = Payment.builder()
                .user(user)
                .order(order)
                .amount(request.getAmount())
                .createdAt(LocalDateTime.now())
                .status("COMPLETED")
                .paymentMethod(request.getPaymentMethod())
                .build();
        paymentRepository.save(payment);

        order.setPayment(payment);
        order.setStatus("PAID");
        orderRepository.save(order);

        return payment;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }
}
