package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.common.dto.response.TokenResponseDto;
import com.example.demo.common.exception.BadRequestException;
import com.example.demo.common.exception.UnauthorizedException;
import com.example.demo.common.exception.payload.ErrorCode;
import com.example.demo.infrastructure.utils.JwtUtil;

class TokenServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateTokens_ShouldReturnTokenResponse_WhenUserIdIsValid() {
        // Given
        String userId = "testUser";
        String accessToken = "accessToken";
        String idToken = "idToken";
        String refreshToken = "refreshToken";

        when(jwtUtil.createToken(userId, 3600000, "IDP", "SP", "ACCESS")).thenReturn(accessToken);
        when(jwtUtil.createToken(userId, 3600000, "IDP", "SP", "ID")).thenReturn(idToken);
        when(jwtUtil.createRefreshToken(userId, 86400000)).thenReturn(refreshToken);

        // When
        TokenResponseDto response = tokenService.generateTokens(userId);

        // Then
        assertEquals(accessToken, response.accessToknen());
        assertEquals(idToken, response.idToken());
        assertEquals(refreshToken, response.refreshToken());
    }

    @Test
    void generateTokens_ShouldThrowUnauthorizedException_WhenJwtUtilFails() {
        // Given
        String userId = "testUser";

        when(jwtUtil.createToken(userId, 3600000, "IDP", "SP", "ACCESS")).thenThrow(new RuntimeException());

        // When & Then
        assertThrows(UnauthorizedException.class, () -> tokenService.generateTokens(userId));
    }

    @Test
    void refreshTokens_ShouldReturnNewAccessToken_WhenRefreshTokenIsValid_WithoutIdToken() {
        // Given
        String refreshToken = "validRefreshToken";
        String userId = "testUser";
        String newAccessToken = "newAccessToken";

        when(jwtUtil.isTokenValid(refreshToken)).thenReturn(true);
        when(jwtUtil.getUserIdFromToken(refreshToken)).thenReturn(userId);
        when(jwtUtil.createToken(userId, 3600000, "IDP", "SP", "ACCESS")).thenReturn(newAccessToken);

        // When
        TokenResponseDto response = tokenService.refreshTokens(refreshToken, false);

        // Then
        assertEquals(newAccessToken, response.accessToknen());
        assertNull(response.idToken());
        assertEquals(refreshToken, response.refreshToken());
    }

    @Test
    void refreshTokens_ShouldReturnNewAccessAndIdToken_WhenRefreshTokenIsValid_WithIdToken() {
        // Given
        String refreshToken = "validRefreshToken";
        String userId = "testUser";
        String newAccessToken = "newAccessToken";
        String newIdToken = "newIdToken";

        when(jwtUtil.isTokenValid(refreshToken)).thenReturn(true);
        when(jwtUtil.getUserIdFromToken(refreshToken)).thenReturn(userId);
        when(jwtUtil.createToken(userId, 3600000, "IDP", "SP", "ACCESS")).thenReturn(newAccessToken);
        when(jwtUtil.createToken(userId, 86400000, "IDP", "SP", "ID")).thenReturn(newIdToken);

        // When
        TokenResponseDto response = tokenService.refreshTokens(refreshToken, true);

        // Then
        assertEquals(newAccessToken, response.accessToknen());
        assertEquals(newIdToken, response.idToken());
        assertEquals(refreshToken, response.refreshToken());
    }

    @Test
    void refreshTokens_ShouldThrowUnauthorizedException_WhenRefreshTokenIsInvalid() {
        // Given
        String refreshToken = "invalidRefreshToken";

        when(jwtUtil.isTokenValid(refreshToken)).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> tokenService.refreshTokens(refreshToken, true));
    }

    @Test
    void refreshTokens_ShouldThrowBadRequestException_WhenTokenFormatIsInvalid() {
        // Given
        String refreshToken = "invalidFormatToken";

        when(jwtUtil.isTokenValid(refreshToken)).thenThrow(new BadRequestException(ErrorCode.TOKEN_NOT_CORRECT_FORMAT));

        // When & Then
        assertThrows(BadRequestException.class, () -> tokenService.refreshTokens(refreshToken, true));
    }
}