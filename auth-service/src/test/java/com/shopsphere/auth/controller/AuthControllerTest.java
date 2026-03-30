package com.shopsphere.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.auth.dto.AuthResponseDto;
import com.shopsphere.auth.dto.LoginRequestDto;
import com.shopsphere.auth.dto.RegisterRequestDto;
import com.shopsphere.auth.enums.Role;
import com.shopsphere.auth.exception.AuthException;
import com.shopsphere.auth.security.CustomUserDetailsService;
import com.shopsphere.auth.service.AuthService;
import com.shopsphere.auth.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test_endpoint_returns200AndMessage() throws Exception {
        mockMvc.perform(get("/auth/test"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().string("Auth Service Working"));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto("john@example.com", "john@example.com" , "password123", Role.CUSTOMER);

        when(authService.register(any(RegisterRequestDto.class)))
                .thenThrow(new AuthException("Email already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()); // adjust based on your GlobalExceptionHandler
    }

    @Test
    void register_adminRole_returns200WithAdminRole() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto("admin@example.com" , "admin@example.com", "adminpass", Role.ADMIN);

        AuthResponseDto response = new AuthResponseDto("admin.jwt.token", "admin@example.com", "ADMIN");

        when(authService.register(any(RegisterRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void login_adminUser_returns200WithAdminRole() throws Exception {
        LoginRequestDto request = new LoginRequestDto("admin@example.com", "adminpass");

        AuthResponseDto response = new AuthResponseDto("admin.jwt.token", "admin@example.com", "ADMIN");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}