package com.example.demo.infrastructure.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

import com.example.demo.common.exception.BadRequestException;
import com.example.demo.common.exception.InternalServerErrorException;
import com.example.demo.common.exception.UnauthorizedException;
import com.example.demo.common.exception.payload.ErrorCode;
import com.example.demo.common.model.TokenPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtUtil {
    
    private final String secretKey = "idpPrivateKey";
    private final String KID = "kid-fixed-key";
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 인스턴스 생성

    public String createToken(String userId, long expirationTime, String issuer, String audience) {
        long expiration = System.currentTimeMillis() + expirationTime;
        TokenPayload tokenPayload = new TokenPayload(userId, expiration, issuer, audience);
        String header = createHeader("HS256", "JWT", KID);
        String payload = createPayload(tokenPayload);
        String signature = generateSignature(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public String createRefreshToken(String userId, long expirationTime) {
        long expiration = System.currentTimeMillis() + expirationTime;
        TokenPayload tokenPayload = new TokenPayload(userId, expiration, "", "");
        String header = createHeader("HS256", "JWT", "");
        String payload = createPayload(tokenPayload);
        String signature = generateSignature(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public boolean isTokenValid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new BadRequestException(ErrorCode.TOKEN_NOT_CORRECT_FORMAT);
            }

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            String decodedPayload;
            try {
                decodedPayload = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(ErrorCode.TOKEN_NOT_CORRECT_FORMAT);
            }

            Map<String, String> payloadMap = objectMapper.readValue(decodedPayload, new TypeReference<Map<String, String>>() {});

            long expiration = Long.parseLong(payloadMap.get("expiration"));
            if (System.currentTimeMillis() > expiration) {
                throw new BadRequestException(ErrorCode.TOKEN_EXPIRATION);
            }

            String dataToSign = header + "." + payload;
            String expectedSignature = generateSignature(dataToSign);

            if (!signature.equals(expectedSignature)) {
                throw new UnauthorizedException(ErrorCode.TOKEN_VALIDATION_FAILED);
            }

            return true;
        } catch (BadRequestException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_VALIDATION_FAILED);
        }
    }

    private String createHeader(String algorithm, String type, String kid) {
        String headerJson = String.format("{\"alg\":\"%s\",\"typ\":\"%s\",\"kid\":\"%s\"}", algorithm, type, kid);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
    }

    private String createPayload(TokenPayload payload) {
        String payloadJson = String.format("{\"userId\":\"%s\",\"expiration\":\"%d\",\"issuer\":\"%s\",\"audience\":\"%s\"}",
                payload.userId(), payload.expiration(), payload.issuer(), payload.audience());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
    }

    private String generateSignature(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((data + secretKey).getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerErrorException(ErrorCode.SIGNATURE_FAILED);
        }
    }
}