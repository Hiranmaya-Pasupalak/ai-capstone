package com.bookworm.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateQuantityRequest {
    @Min(1)
    private int quantity;
}
