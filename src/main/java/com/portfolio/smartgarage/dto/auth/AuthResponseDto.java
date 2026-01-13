package com.portfolio.smartgarage.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String role;
}

