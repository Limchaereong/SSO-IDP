package com.example.demo.common.model;

public record TokenPayload(String userId,
							long expiration,
							String issuer,
							String audience,
							String tokenType
) {
}