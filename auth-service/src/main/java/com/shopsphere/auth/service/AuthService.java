package com.shopsphere.auth.service;

import com.shopsphere.auth.dto.*;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto request);
    AuthResponseDto login(LoginRequestDto request);
}
