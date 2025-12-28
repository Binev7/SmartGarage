package com.portfolio.smartgarage.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank @Size(min = 2, max = 20)
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank @Pattern(regexp = "^[0-9]{10}$")
    private String phoneNumber;

    @NotBlank @Size(min = 8)
    private String password;
}
