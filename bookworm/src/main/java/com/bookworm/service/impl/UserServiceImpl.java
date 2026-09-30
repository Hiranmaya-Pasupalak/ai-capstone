package com.bookworm.service.impl;

import com.bookworm.dto.*;
import com.bookworm.entity.*;
import com.bookworm.exception.*;
import com.bookworm.mapper.EntityMapper;
import com.bookworm.repository.*;
import com.bookworm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getProfile(String userEmail) {
        return mapper.toDto(getUser(userEmail));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> listAddresses(String userEmail) {
        User user = getUser(userEmail);
        return addressRepository.findByUserId(user.getId()).stream()
            .map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDto addAddress(String userEmail, AddressRequest request) {
        User user = getUser(userEmail);
        Address address = mapper.toEntity(request, user);
        return mapper.toDto(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(String userEmail, String addressId) {
        User user = getUser(userEmail);
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));
        addressRepository.delete(address);
    }

    @Override
    @Transactional(readOnly = true)
    public GiftPointsDto getGiftPoints(String userEmail) {
        User user = getUser(userEmail);
        int balance = user.getGiftPointsBalance();
        return GiftPointsDto.builder()
            .balance(balance)
            .equivalentValue(balance) // 1 point = 1 INR
            .build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
