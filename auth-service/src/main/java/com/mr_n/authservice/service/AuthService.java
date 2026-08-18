package com.mr_n.authservice.service;

import com.mr_n.authservice.feign.UserClient;
import com.mr_n.authservice.dto.*;
import com.mr_n.authservice.model.RefreshToken;
import com.mr_n.authservice.repository.RefreshTokenRepository;
import com.mr_n.authservice.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    public UserDto register(RegisterRequest request) {
        // Hash password using BCrypt before storing in user-service
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Hardcode default ROLE_USER for public registrations
        CreateUserRequest internalRequest = CreateUserRequest.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .fullName(request.getFullName())
                .roles(java.util.Set.of("ROLE_USER"))
                .build();

        ResponseEntity<UserDto> response = userClient.createUserInternal(internalRequest);
        return response.getBody();
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        ResponseEntity<UserDto> response = userClient.getUserByUsernameInternal(request.getUsername());
        UserDto user = response.getBody();

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String accessToken = jwtUtils.generateAccessToken(user.getId(), user.getUsername(), user.getRoles());
        RefreshToken refreshToken = createRefreshToken(user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles())
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired. Please log in again.");
        }

        ResponseEntity<UserDto> response = userClient.getUserByUsernameInternal(refreshToken.getUsername());
        UserDto user = response.getBody();

        if (user == null) {
            throw new RuntimeException("User not found for token");
        }

        String newAccessToken = jwtUtils.generateAccessToken(user.getId(), user.getUsername(), user.getRoles());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles())
                .build();
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenRepository.deleteByToken(refreshTokenStr);
    }

    private RefreshToken createRefreshToken(String username) {
        refreshTokenRepository.deleteByUsername(username);

        RefreshToken refreshToken = RefreshToken.builder()
                .username(username)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }
}
