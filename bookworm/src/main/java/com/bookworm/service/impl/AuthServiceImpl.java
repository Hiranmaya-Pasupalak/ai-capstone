package com.bookworm.service.impl;

import com.bookworm.dto.RegisterRequest;
import com.bookworm.dto.UserProfileDto;
import com.bookworm.entity.User;
import com.bookworm.exception.BadRequestException;
import com.bookworm.mapper.EntityMapper;
import com.bookworm.repository.UserRepository;
import com.bookworm.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public UserProfileDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }
        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phoneNumber(request.getPhoneNumber())
            .build();
        return mapper.toDto(userRepository.save(user));
    }
}
