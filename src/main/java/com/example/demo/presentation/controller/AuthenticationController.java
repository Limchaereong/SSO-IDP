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

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<TokenResponseDto>> login(@RequestBody AuthenticationRequestDto request, HttpSession session) {
		TokenResponseDto tokens = authenticationService.authenticate(request, session);
		return ResponseEntity.ok(ApiResponse.success(tokens));
	}

}
