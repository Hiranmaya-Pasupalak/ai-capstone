package com.bookworm.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceOrderRequest {

    @NotNull
    private DeliveryAddressInput deliveryAddress;

    private boolean redeemGiftPoints = false;

    @Data
    public static class DeliveryAddressInput {
        // Either addressId (saved address) or full inline address
        private String addressId;

        // Inline address fields (used when addressId is null)
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
}
