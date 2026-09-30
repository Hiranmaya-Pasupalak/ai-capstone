package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class GiftPointsDto {
    private int balance;
    private double equivalentValue; // INR value of points
}
