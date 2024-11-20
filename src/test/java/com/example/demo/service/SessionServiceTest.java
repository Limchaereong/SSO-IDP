package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpSession;

class SessionServiceTest {

    @Mock
    private HttpSession session;

    @InjectMocks
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSession_ShouldSetUserIdAndMaxInactiveInterval() {
        // Given
        String userId = "testUser";

        // When
        sessionService.createSession(session, userId);

        // Then
        verify(session, times(1)).setAttribute("userId", userId);
        verify(session, times(1)).setMaxInactiveInterval(3600);
    }

    @Test
    void isSessionValid_ShouldReturnTrue_WhenUserIdIsPresent() {
        // Given
        when(session.getAttribute("userId")).thenReturn("testUser");

        // When
        boolean isValid = sessionService.isSessionValid(session);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isSessionValid_ShouldReturnFalse_WhenUserIdIsNotPresent() {
        // Given
        when(session.getAttribute("userId")).thenReturn(null);

        // When
        boolean isValid = sessionService.isSessionValid(session);

        // Then
        assertFalse(isValid);
    }

    @Test
    void invalidateSession_ShouldInvalidateSession() {
        // When
        sessionService.invalidateSession(session);

        // Then
        verify(session, times(1)).invalidate();
    }
}