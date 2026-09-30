package com.bookworm.controller;

import com.bookworm.dto.*;
import com.bookworm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileDto> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> listAddresses(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.listAddresses(userDetails.getUsername()));
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(201).body(userService.addAddress(userDetails.getUsername(), request));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String addressId) {
        userService.deleteAddress(userDetails.getUsername(), addressId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/gift-points")
    public ResponseEntity<GiftPointsDto> getGiftPoints(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getGiftPoints(userDetails.getUsername()));
    }
}
