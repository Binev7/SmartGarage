package com.portfolio.smartgarage.mapper;

import com.portfolio.smartgarage.dto.RegisterRequest;
import com.portfolio.smartgarage.dto.AuthResponse;
import com.portfolio.smartgarage.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        return user;
    }

    public AuthResponse toAuthResponse(User user, String token) {
        if (user == null) {
            return null;
        }

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
