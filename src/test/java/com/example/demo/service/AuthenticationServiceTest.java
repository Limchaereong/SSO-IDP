//package com.example.demo.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.when;
//
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import com.example.demo.common.dto.request.AuthenticationRequestDto;
//import com.example.demo.common.dto.response.TokenResponseDto;
//import com.example.demo.common.exception.NotFoundException;
//import com.example.demo.common.exception.UnauthorizedException;
//import com.example.demo.common.model.User;
//import com.example.demo.common.exception.payload.ErrorCode;
//import com.example.demo.persistence.UserRepository;
//
//class AuthenticationServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private TokenService tokenService;
//
//    @InjectMocks
//    private AuthenticationService authenticationService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void authenticate_ShouldReturnTokens_WhenCredentialsAreValid() {
//        // Given
//        String username = "testUser";
//        String password = "password";
//        User user = new User("userId", username, password);
//        AuthenticationRequestDto request = new AuthenticationRequestDto(username, password);
//        TokenResponseDto tokenResponse = new TokenResponseDto("accessToken", "idToken", "refreshToken");
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
//        when(tokenService.generateTokens(user.userId())).thenReturn(tokenResponse);
//
//        // When
//        TokenResponseDto result = authenticationService.authenticate(request);
//
//        // Then
//        assertEquals(tokenResponse, result);
//    }
//
//    @Test
//    void authenticate_ShouldThrowNotFoundException_WhenUserNotFound() {
//        // Given
//        String username = "nonExistentUser";
//        AuthenticationRequestDto request = new AuthenticationRequestDto(username, "password");
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(NotFoundException.class, () -> authenticationService.authenticate(request));
//    }
//
//    @Test
//    void authenticate_ShouldThrowUnauthorizedException_WhenPasswordDoesNotMatch() {
//        // Given
//        String username = "testUser";
//        String correctPassword = "password";
//        String incorrectPassword = "wrongPassword";
//        User user = new User("userId", username, correctPassword);
//        AuthenticationRequestDto request = new AuthenticationRequestDto(username, incorrectPassword);
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
//
//        // When & Then
//        assertThrows(UnauthorizedException.class, () -> authenticationService.authenticate(request));
//    }
//}