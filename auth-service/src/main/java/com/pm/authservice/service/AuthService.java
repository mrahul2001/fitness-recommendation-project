package com.pm.authservice.service;

import com.pm.authservice.dto.AuthResponseDTO;
import com.pm.authservice.dto.TokenValidationResponseDTO;
import com.pm.authservice.dto.UserResponseDTO;
import com.pm.authservice.dto.UserServiceResponseDTO;
import com.pm.authservice.model.UserRole;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UsersServiceClient usersServiceClient;

    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    public AuthResponseDTO login(String email, String password) {

        UserServiceResponseDTO user = usersServiceClient.getUserByEmail(email);

        if (user.getPassword() == null) {
            throw new RuntimeException("This account uses Google Login");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return issueTokens(user);
    }

    private AuthResponseDTO issueTokens(UserServiceResponseDTO user) {

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                user.getUserRole().name(),
                user.getId().toString()
        );

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        refreshTokenStore.put(user.getEmail(), refreshToken);

        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId(user.getId());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setUserRole(user.getUserRole());
        userResponseDTO.setAuthProvider(user.getAuthProvider());

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userResponseDTO)
                .build();
    }

    public AuthResponseDTO handleGoogleLogin(UserServiceResponseDTO user) {
        return issueTokens(user);
    }

    public AuthResponseDTO refreshToken(String refreshToken) {

        Claims claims = jwtService.validateAndParseClaims(refreshToken);

        String email = claims.getSubject();

        if (!refreshToken.equals(refreshTokenStore.get(email))) {
            throw new RuntimeException("Invalid refresh token");
        }

        UserServiceResponseDTO user = usersServiceClient.getUserByEmail(email);

        return issueTokens(user);
    }

    public TokenValidationResponseDTO validateToken(String token) {

        try {

            Claims claims = jwtService.validateAndParseClaims(token);

            return new TokenValidationResponseDTO(
                    true,
                    claims.getSubject(),
                    UserRole.valueOf(claims.get("role").toString()),
                    claims.get("userID", String.class)
            );

        } catch (Exception e) {

            return new TokenValidationResponseDTO(
                    false,
                    null,
                    null,
                    null
            );
        }
    }

    public void logout(String token) {

        Claims claims = jwtService.validateAndParseClaims(token);

        refreshTokenStore.remove(claims.getSubject());
    }
}