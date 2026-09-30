package com.bookworm.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank private String addressLine1;
    private String addressLine2;
    @NotBlank private String city;
    @NotBlank private String pin;
    @NotBlank private String state;
    @NotBlank private String country;
    @NotBlank @Email private String email;
    @NotBlank private String phoneNumber;
}
