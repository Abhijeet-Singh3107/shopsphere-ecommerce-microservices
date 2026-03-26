package com.shopsphere.auth.service;

import com.shopsphere.auth.dto.AuthResponseDto;
import com.shopsphere.auth.dto.LoginRequestDto;
import com.shopsphere.auth.dto.RegisterRequestDto;
import com.shopsphere.auth.entity.User;
import com.shopsphere.auth.exception.AuthException;
import com.shopsphere.auth.repository.UserRepository;
import com.shopsphere.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {
        // check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Email already registered: "
                    + request.getEmail());
        }

        // build and save user with encoded password
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        // generate token immediately after registration
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponseDto(token, user.getEmail(),
                user.getRole().name());
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {

        // this throws exception automatically if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // here means credentials are valid
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new AuthException("User not found: "
                                + request.getEmail()));

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponseDto(token, user.getEmail(),
                user.getRole().name());
    }
}
