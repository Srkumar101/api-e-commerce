package com.ecommerce.backend.payload;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long productId;
    private int quantity;
}
