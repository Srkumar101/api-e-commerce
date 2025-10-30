package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.payload.CartItemRequest;
import com.ecommerce.backend.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> viewCart() {
        try {
            return ResponseEntity.ok(cartService.viewCart());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemRequest request) {
        try {
            return ResponseEntity.ok(cartService.addToCart(request));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException | IllegalStateException err) {
            return ResponseEntity.badRequest().body(err.getMessage());
        }
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId, @RequestParam int quantity) {
        try {
            return ResponseEntity.ok(cartService.updateCartItem(cartItemId, quantity));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long cartItemId) {
        try {
            return ResponseEntity.ok(cartService.removeCartItem(cartItemId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
