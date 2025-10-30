package com.ecommerce.backend.payload;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private Long categoryId;
}
