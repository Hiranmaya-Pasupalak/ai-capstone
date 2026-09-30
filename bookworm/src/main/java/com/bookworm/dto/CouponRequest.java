package com.bookworm.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CouponRequest {
    @NotBlank
    private String couponCode;
}
