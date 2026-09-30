package com.bookworm.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotBlank
    private String bookId;

    @Min(1)
    private int quantity;
}
