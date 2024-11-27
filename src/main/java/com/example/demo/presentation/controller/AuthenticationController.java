package com.example.demo.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.dto.request.AuthenticationRequestDto;
import com.example.demo.common.dto.response.TokenResponseDto;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.service.AuthenticationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody AuthenticationRequestDto request, HttpServletResponse response) {
        TokenResponseDto tokens = authenticationService.authenticate(request);
        
        Cookie accessTokenCookie = new Cookie("accessToken", tokens.accessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.refreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
        
        return ResponseEntity.ok(ApiResponse.success("Login successful"));
    }
    
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<TokenResponseDto>> login(@RequestBody AuthenticationRequestDto request, HttpServletResponse response) {
//        TokenResponseDto tokens = authenticationService.authenticate(request);
//        
//        return ResponseEntity.ok(ApiResponse.success(tokens));
//    }
}