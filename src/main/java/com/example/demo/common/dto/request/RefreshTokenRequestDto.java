package com.example.demo.common.dto.request;

public record RefreshTokenRequestDto(String refreshToken,
									boolean includedIdToken) {

}
