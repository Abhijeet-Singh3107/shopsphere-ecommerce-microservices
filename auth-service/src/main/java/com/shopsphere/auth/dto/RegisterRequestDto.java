package com.shopsphere.auth.dto;

import com.shopsphere.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    private String name;
    private String email;
    private String password;
    private Role role;
}
