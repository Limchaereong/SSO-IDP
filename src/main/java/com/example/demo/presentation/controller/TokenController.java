package com.example.demo.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.dto.request.RefreshTokenRequestDto;
import com.example.demo.common.dto.response.TokenResponseDto;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.service.TokenService;

@RestController
@RequestMapping("/token")
public class TokenController {
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<TokenResponseDto>> refreshTokens(@RequestBody RefreshTokenRequestDto request) {
		TokenResponseDto tokens = tokenService.refreshTokens(request.refreshToken(), request.includedIdToken());
		return ResponseEntity.ok(ApiResponse.success(tokens));
	}
	
}
