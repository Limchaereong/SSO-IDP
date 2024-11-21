package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.common.exception.NotFoundException;
import com.example.demo.common.exception.UnauthorizedException;
import com.example.demo.common.exception.payload.ErrorCode;
import com.example.demo.common.dto.request.AuthenticationRequestDto;
import com.example.demo.common.dto.response.TokenResponseDto;
import com.example.demo.common.model.User;
import com.example.demo.common.response.ApiResponse;
import com.example.demo.persistence.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class AuthenticationService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;
    
    public TokenResponseDto authenticate(AuthenticationRequestDto request) {
        Optional<User> userOpt = userRepository.findByUsername(request.username());
        
        if(userOpt.isEmpty()) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_USER);
        }
        
        User user = userOpt.get();
        
        if(!user.password().equals(request.password())) {
            throw new UnauthorizedException(ErrorCode.PASSWORD_NOT_CORRECT);
        }
        
        return tokenService.generateTokens(user.userId());
    }
}
