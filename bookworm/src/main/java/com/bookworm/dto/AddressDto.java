package com.bookworm.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AddressDto {
    private String id;
    private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String pin;
    private String state;
    private String country;
    private String email;
    private String phoneNumber;
}
