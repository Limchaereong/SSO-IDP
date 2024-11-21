package com.example.demo.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.dto.request.RefreshTokenRequestDto;
import com.example.demo.common.dto.response.TokenResponseDto;
import com.example.demo.common.exception.UnauthorizedException;
import com.example.demo.common.exception.payload.ErrorCode;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.service.TokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/token")
public class TokenController {
    
    @Autowired
    private TokenService tokenService;
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshTokens(@RequestBody RefreshTokenRequestDto request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String refreshToken = null;
        for (Cookie cookie : httpRequest.getCookies()) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
                break;
            }
        }
        
        if (refreshToken == null) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }
        
        TokenResponseDto tokens = tokenService.refreshTokens(refreshToken, request.includedIdToken());
        
        Cookie accessTokenCookie = new Cookie("accessToken", tokens.accessToknen());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        httpResponse.addCookie(accessTokenCookie);
        
        if (tokens.idToken() != null) {
            Cookie idTokenCookie = new Cookie("idToken", tokens.idToken());
            idTokenCookie.setHttpOnly(true);
            idTokenCookie.setPath("/");
            httpResponse.addCookie(idTokenCookie);
        }
        
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.refreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        httpResponse.addCookie(refreshTokenCookie);
        
        return ResponseEntity.ok(ApiResponse.success("Tokens refreshed successfully"));
    }
}
