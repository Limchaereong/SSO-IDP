package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.example.demo.common.dto.request.AuthenticationRequestDto;
import com.example.demo.common.dto.response.TokenResponseDto;
import com.example.demo.common.exception.NotFoundException;
import com.example.demo.common.exception.UnauthorizedException;
import com.example.demo.common.model.User;
import com.example.demo.persistence.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private SessionService sessionService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_ShouldReturnTokenResponse_WhenCredentialsAreValid() {
        // Given
        String userId = "12345";
        String username = "testUser";
        String password = "testPassword";
        AuthenticationRequestDto request = new AuthenticationRequestDto(username, password);

        User user = new User(userId, username, password);
        TokenResponseDto expectedResponse = new TokenResponseDto("accessToken", "idToken", "refreshToken");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(tokenService.generateTokens(userId)).thenReturn(expectedResponse);
        doNothing().when(sessionService).createSession(session, userId);

        // When
        TokenResponseDto response = authenticationService.authenticate(request, session);

        // Then
        assert (response.equals(expectedResponse));
    }

    @Test
    void authenticate_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Given
        String username = "invalidUser";
        String password = "testPassword";
        AuthenticationRequestDto request = new AuthenticationRequestDto(username, password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> authenticationService.authenticate(request, session));
    }

    @Test
    void authenticate_ShouldThrowUnauthorizedException_WhenPasswordIsIncorrect() {
        // Given
        String userId = "12345";
        String username = "testUser";
        String incorrectPassword = "wrongPassword";
        AuthenticationRequestDto request = new AuthenticationRequestDto(username, incorrectPassword);

        User user = new User(userId, username, "correctPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(UnauthorizedException.class, () -> authenticationService.authenticate(request, session));
    }
}