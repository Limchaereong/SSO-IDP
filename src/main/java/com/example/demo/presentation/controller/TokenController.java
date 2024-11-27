package com.example.demo.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.dto.response.TokenResponseDto;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.service.TokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/token")
public class TokenController {

    private final TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/generateIdToken")
    public ResponseEntity<ApiResponse<String>> generateIdToken(
            @RequestHeader("accessToken") String accessToken,
            @RequestHeader("SP-Identifier") String spIdentifier,
            HttpServletResponse response) {

        String userId = tokenService.getUserIdFromAccessToken(accessToken);
        String idToken = tokenService.generateIdToken(userId, spIdentifier);

        TokenResponseDto tokens = tokenService.generateTokens(userId);

        addTokenCookies(response, tokens);

        Cookie idTokenCookie = new Cookie("idToken", idToken);
        idTokenCookie.setHttpOnly(true);
        idTokenCookie.setPath("/");
        response.addCookie(idTokenCookie);

        return ResponseEntity.ok(ApiResponse.success(idToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponseDto>> refreshAccessToken(
            @RequestHeader("refreshToken") String refreshToken,
            HttpServletResponse response) {

        TokenResponseDto tokens = tokenService.refreshAccessToken(refreshToken);

        addTokenCookies(response, tokens);

        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    private void addTokenCookies(HttpServletResponse response, TokenResponseDto tokens) {
        Cookie accessTokenCookie = new Cookie("accessToken", tokens.accessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.refreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
    }
}