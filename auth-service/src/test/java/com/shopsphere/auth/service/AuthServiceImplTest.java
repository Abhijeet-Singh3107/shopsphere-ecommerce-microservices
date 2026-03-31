package com.shopsphere.auth.service;

import com.shopsphere.auth.dto.AuthResponseDto;
import com.shopsphere.auth.dto.LoginRequestDto;
import com.shopsphere.auth.dto.RegisterRequestDto;
import com.shopsphere.auth.entity.User;
import com.shopsphere.auth.enums.Role;
import com.shopsphere.auth.exception.AuthException;
import com.shopsphere.auth.repository.UserRepository;
import com.shopsphere.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDto();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@gmail.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.CUSTOMER);

        loginRequest = new LoginRequestDto();
        loginRequest.setEmail("john@gmail.com");
        loginRequest.setPassword("password123");

        savedUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@gmail.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .build();
    }

    // ─── register() ───────────────────────────────────────────────

    @Test
    void register_success_returnsTokenAndUserInfo() {
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtUtil.generateToken("john@gmail.com", "CUSTOMER")).thenReturn("mock-jwt-token");

        AuthResponseDto response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("mock-jwt-token");
        assertThat(response.getEmail()).isEqualTo("john@gmail.com");
        assertThat(response.getRole()).isEqualTo("CUSTOMER");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsAuthException() {
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("john@gmail.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_passwordIsEncoded_rawPasswordNeverSaved() {
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(any(), any())).thenReturn("mock-jwt-token");

        authService.register(registerRequest);

        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository).save(argThat(user ->
                user.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    void register_jwtGeneratedWithCorrectEmailAndRole() {
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken("john@gmail.com", "CUSTOMER")).thenReturn("mock-jwt-token");

        authService.register(registerRequest);

        verify(jwtUtil, times(1)).generateToken("john@gmail.com", "CUSTOMER");
    }

    @Test
    void register_adminRole_success() {
        registerRequest.setRole(Role.ADMIN);
        when(userRepository.existsByEmail("john@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken("john@gmail.com", "ADMIN")).thenReturn("mock-jwt-token");

        AuthResponseDto response = authService.register(registerRequest);

        assertThat(response.getRole()).isEqualTo("ADMIN");
        verify(jwtUtil, times(1)).generateToken("john@gmail.com", "ADMIN");
    }

    // ─── login() ──────────────────────────────────────────────────

    @Test
    void login_success_returnsTokenAndUserInfo() {
        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(savedUser));
        when(jwtUtil.generateToken("john@gmail.com", "CUSTOMER")).thenReturn("mock-jwt-token");

        AuthResponseDto response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("mock-jwt-token");
        assertThat(response.getEmail()).isEqualTo("john@gmail.com");
        assertThat(response.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    void login_badCredentials_throwsException() {
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void login_userNotFound_throwsAuthException() {
        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("john@gmail.com");
    }

    @Test
    void login_jwtGeneratedWithCorrectEmailAndRole() {
        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(savedUser));
        when(jwtUtil.generateToken("john@gmail.com", "CUSTOMER")).thenReturn("mock-jwt-token");

        authService.login(loginRequest);

        verify(jwtUtil, times(1)).generateToken("john@gmail.com", "CUSTOMER");
    }

    @Test
    void login_adminUser_success() {
        User adminUser = User.builder()
                .id(2L)
                .name("Admin User")
                .email("john@gmail.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(adminUser));
        when(jwtUtil.generateToken("john@gmail.com", "ADMIN")).thenReturn("mock-admin-token");

        AuthResponseDto response = authService.login(loginRequest);

        assertThat(response.getRole()).isEqualTo("ADMIN");
        assertThat(response.getToken()).isEqualTo("mock-admin-token");
    }
}