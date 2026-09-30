package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class UserProfileDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private int giftPointsBalance;
}
