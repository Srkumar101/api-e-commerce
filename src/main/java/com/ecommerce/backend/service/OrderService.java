package com.ecommerce.backend.service;

import com.ecommerce.backend.model.*;
import com.ecommerce.backend.payload.OrderRequest;
import com.ecommerce.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findById(user.getCart().getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found."));
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("No items in the cart to place order.");
        }
        Address address = addressRepository.findById(request.getDeliveryAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Delivery address not found."));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Cannot use another user's address.");
        }

        Order order = Order.builder()
                .user(user)
                .address(address)
                .createdAt(LocalDateTime.now())
                .status("PENDING")
                .build();
        order = orderRepository.save(order);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found."));
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();
            orderItemRepository.save(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotalAmount(total);
        orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);
        return order;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }
}
