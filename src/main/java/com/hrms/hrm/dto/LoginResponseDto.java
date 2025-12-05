package com.hrms.hrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private String token;
    private String role;
    private UUID employeeId;
    private String refreshToken;
    private String username;
    private Long expiresIn;
}
