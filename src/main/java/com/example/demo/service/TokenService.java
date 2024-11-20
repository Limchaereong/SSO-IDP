package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.common.dto.response.TokenResponseDto;
import com.example.demo.common.exception.UnauthorizedException;
import com.example.demo.common.exception.payload.ErrorCode;
import com.example.demo.infrastructure.utils.JwtUtil;

@Service
public class TokenService {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	public TokenResponseDto generateTokens(String userId) {
		try {
			String accessToken = jwtUtil.createToken(userId, 3600000, "IDP", "SP", "ACCESS");
			String idToken = jwtUtil.createToken(userId, 3600000, "IDP", "SP", "ID");
			String refreshToken = jwtUtil.createRefreshToken(userId, 86400000);
			
			return new TokenResponseDto(accessToken, idToken, refreshToken);
		} catch(Exception e) {
			throw new UnauthorizedException(ErrorCode.TOKEN_GENERATION_FAILED);
		}
	}
	
	public TokenResponseDto refreshTokens(String refreshToken, boolean includeIdToken) {
        if (jwtUtil.isTokenValid(refreshToken)) {
            String userId = jwtUtil.getUserIdFromToken(refreshToken);
            String newAccessToken = jwtUtil.createToken(userId, 3600000, "IDP", "SP", "ACCESS");
            String newIdToken = includeIdToken ? jwtUtil.createToken(userId, 86400000, "IDP", "SP", "ID") : null;
            

            return new TokenResponseDto(newAccessToken, newIdToken, refreshToken);
        } else {
            throw new UnauthorizedException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }

}
