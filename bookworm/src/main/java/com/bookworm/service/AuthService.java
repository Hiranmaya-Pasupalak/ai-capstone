package com.bookworm.service;

import com.bookworm.dto.RegisterRequest;
import com.bookworm.dto.UserProfileDto;

public interface AuthService {
    UserProfileDto register(RegisterRequest request);
}
