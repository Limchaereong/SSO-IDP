package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.common.dto.response.TokenResponseDto;
import com.example.demo.common.exception.InternalServerErrorException;
import com.example.demo.common.exception.UnauthorizedException;
import com.example.demo.common.exception.payload.ErrorCode;
import com.example.demo.common.model.User;
import com.example.demo.infrastructure.utils.JwtUtil;
import com.example.demo.persistence.UserRepository;

@Service
public class TokenService {
	
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public TokenService(JwtUtil jwtUtil, UserRepository userRepository) {
    	this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public TokenResponseDto generateTokens(String userId) {
        try {
            String accessToken = jwtUtil.createToken(userId, 360000, "IDP", "SP", "ACCESS"); //360000
            String refreshToken = jwtUtil.createRefreshToken(userId, 86400000);
            return new TokenResponseDto(accessToken, null, refreshToken);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }

    public String generateIdToken(String userId, String spIdentifier) {
        try {
//            User user = userRepository.findByUserId(userId)
//                    .orElseThrow(() -> {
//                        System.out.println("User not found for userId: " + userId);
//                        return new UnauthorizedException(ErrorCode.NOT_FOUND_USER);
//                    });
        	
        	String email = "sa990422@gmail.com";
        	String phoneNumber = "010-6615-7835";

            String additionalInfo;
            String audience;

            switch (spIdentifier) {
                case "SP1":
                    additionalInfo = email;
                    audience = "SP1";
                    break;
                case "SP2":
                    additionalInfo = phoneNumber;
                    audience = "SP2";
                    break;
                default:
                    throw new UnauthorizedException(ErrorCode.TOKEN_RE_GENERATION_FAILED);
            }

            return jwtUtil.createIdTokenWithAdditionalInfo(userId, additionalInfo, 360000, "IDP", audience);
        } catch (UnauthorizedException e) {
            System.err.println("UnauthorizedException: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String getUserIdFromAccessToken(String accessToken) {
        if (accessToken == null || !jwtUtil.isTokenValid(accessToken)) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }
        return jwtUtil.getUserIdFromToken(accessToken);
    }
}