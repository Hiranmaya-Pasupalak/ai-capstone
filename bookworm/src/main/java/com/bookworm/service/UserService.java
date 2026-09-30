package com.bookworm.service;

import com.bookworm.dto.*;
import java.util.List;

public interface UserService {
    UserProfileDto getProfile(String userEmail);
    List<AddressDto> listAddresses(String userEmail);
    AddressDto addAddress(String userEmail, AddressRequest request);
    void deleteAddress(String userEmail, String addressId);
    GiftPointsDto getGiftPoints(String userEmail);
}
