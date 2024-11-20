package com.example.demo.infrastructure.utils;

import com.example.demo.common.exception.BadRequestException;
import com.example.demo.common.exception.UnauthorizedException;
import com.example.demo.infrastructure.utils.JwtUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    public void testCreateToken() {
        String userId = "user123";
        long expirationTime = 3600000L;
        String issuer = "exampleIssuer";
        String audience = "exampleAudience";

        String token = jwtUtil.createToken(userId, expirationTime, issuer, audience);
        Assertions.assertNotNull(token, "Token should not be null");
        Assertions.assertTrue(token.contains("."), "Token should contain '.' to separate header, payload, and signature");
    }

    @Test
    public void testCreateRefreshToken() {
        String userId = "user123";
        long expirationTime = 7200000L;

        String refreshToken = jwtUtil.createRefreshToken(userId, expirationTime);
        Assertions.assertNotNull(refreshToken, "Refresh Token should not be null");
        Assertions.assertTrue(refreshToken.contains("."), "Refresh Token should contain '.' to separate header, payload, and signature");
    }

    @Test
    public void testIsTokenValid_Success() {
        String userId = "user123";
        long expirationTime = 3600000L;
        String issuer = "exampleIssuer";
        String audience = "exampleAudience";

        String token = jwtUtil.createToken(userId, expirationTime, issuer, audience);
        boolean isValid = jwtUtil.isTokenValid(token);
        Assertions.assertTrue(isValid, "Token should be valid");
    }

    @Test
    public void testIsTokenValid_Expired() {
        String userId = "user123";
        long expirationTime = -10000L;
        String issuer = "exampleIssuer";
        String audience = "exampleAudience";

        String token = jwtUtil.createToken(userId, expirationTime, issuer, audience);
        Assertions.assertThrows(BadRequestException.class, () -> jwtUtil.isTokenValid(token), "Expired token should throw BadRequestException");
    }

    @Test
    public void testIsTokenValid_InvalidFormat() {
        String invalidToken = "invalid.token.format";

        Assertions.assertThrows(BadRequestException.class, () -> jwtUtil.isTokenValid(invalidToken), "Invalid format token should throw BadRequestException");
    }

    @Test
    public void testIsTokenValid_SignatureMismatch() {
        String userId = "user123";
        long expirationTime = 3600000L;
        String issuer = "exampleIssuer";
        String audience = "exampleAudience";

        String token = jwtUtil.createToken(userId, expirationTime, issuer, audience);
        String[] parts = token.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + "." + "tamperedSignature";

        Assertions.assertThrows(UnauthorizedException.class, () -> jwtUtil.isTokenValid(tamperedToken), "Tampered token should throw UnauthorizedException");
    }
} 