package com.ecommerce.backend.service;

import com.ecommerce.backend.model.*;
import com.ecommerce.backend.payload.CartItemRequest;
import com.ecommerce.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public Cart viewCart() {
        User user = getCurrentUser();
        return user.getCart();
    }

    @Transactional
    public Cart addToCart(CartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = user.getCart();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found."));
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (product.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for product.");
        }
        List<CartItem> cartItems = cart.getCartItems();
        CartItem cartItem = null;
        if (cartItems != null) {
            cartItem = cartItems.stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);
        }
        if (cartItem != null) {
            int newQty = cartItem.getQuantity() + request.getQuantity();
            if (product.getStock() < newQty) {
                throw new IllegalArgumentException("Insufficient stock for combined quantity.");
            }
            cartItem.setQuantity(newQty);
            cartItemRepository.save(cartItem);
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(cartItem);
        }
        cart = cartRepository.findById(cart.getId()).get();
        return cart;
    }

    @Transactional
    public Cart updateCartItem(Long cartItemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        User user = getCurrentUser();
        Cart cart = user.getCart();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found."));
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Cannot update another user's cart item.");
        }
        Product product = cartItem.getProduct();
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product.");
        }
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return cartRepository.findById(cart.getId()).get();
    }

    @Transactional
    public Cart removeCartItem(Long cartItemId) {
        User user = getCurrentUser();
        Cart cart = user.getCart();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found."));
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Cannot remove another user's cart item.");
        }
        cartItemRepository.delete(cartItem);
        return cartRepository.findById(cart.getId()).get();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }
}
