package com.example.demo.infrastructure.utils;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.demo.common.exception.BadRequestException;
import com.example.demo.common.exception.InternalServerErrorException;
import com.example.demo.common.exception.UnauthorizedException;
import com.example.demo.common.exception.payload.ErrorCode;
import com.example.demo.common.model.TokenPayload;
import com.example.demo.infrastructure.keyPair.KeyPairProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtUtil {

    private final KeyPairProvider keyPairProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtUtil(KeyPairProvider keyPairProvider) {
        this.keyPairProvider = keyPairProvider;
    }

    public String createToken(String userId, long expirationTime, String issuer, String audience, String tokenType) {
        try {
            long expiration = System.currentTimeMillis() + expirationTime;
            TokenPayload tokenPayload = new TokenPayload(userId, expiration, issuer, audience, tokenType);
            String header = createHeader("RS256", "JWT");
            String payload = createPayload(tokenPayload);
            String signature = generateSignature(header + "." + payload);
            return header + "." + payload + "." + signature;
        } catch (Exception e) {
            throw new InternalServerErrorException(ErrorCode.SIGNATURE_FAILED);
        }
    }

    public String createIdTokenWithAdditionalInfo(String userId, String additionalInfo, long expirationTime, String issuer, String audience) {
        try {
            long expiration = System.currentTimeMillis() + expirationTime;
            String payloadJson = String.format("{\"userId\":\"%s\",\"expiration\":\"%d\",\"issuer\":\"%s\",\"audience\":\"%s\",\"additionalInfo\":\"%s\"}",
                    userId, expiration, issuer, audience, additionalInfo);
            String header = createHeader("RS256", "JWT");
            String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            String signature = generateSignature(header + "." + payload);
            return header + "." + payload + "." + signature;
        } catch (Exception e) {
            throw new InternalServerErrorException(ErrorCode.SIGNATURE_FAILED);
        }
    }

    public String createRefreshToken(String userId, long expirationTime) {
        long expiration = System.currentTimeMillis() + expirationTime;
        TokenPayload tokenPayload = new TokenPayload(userId, expiration, "", "", "REFRESH");
        String header = createHeader("HS256", "JWT");
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

            String payload = parts[1];

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

            return true;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_VALIDATION_FAILED);
        }
    }

    public String getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new BadRequestException(ErrorCode.TOKEN_NOT_CORRECT_FORMAT);
            }

            String payload = parts[1];
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);

            Map<String, String> payloadMap = objectMapper.readValue(decodedPayload, new TypeReference<Map<String, String>>() {});

            return payloadMap.get("userId");
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_VALIDATION_FAILED);
        }
    }

    private String createHeader(String algorithm, String type) {
        String headerJson = String.format("{\"alg\":\"%s\",\"typ\":\"%s\"}", algorithm, type);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
    }

    private String createPayload(TokenPayload payload) {
        String payloadJson = String.format("{\"userId\":\"%s\",\"expiration\":\"%d\",\"issuer\":\"%s\",\"audience\":\"%s\"}",
                payload.userId(), payload.expiration(), payload.issuer(), payload.audience());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
    }

    private String generateSignature(String data) {
        try {
            PrivateKey privateKey = keyPairProvider.getPrivateKey();

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));

            byte[] signedBytes = signature.sign();
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signedBytes);
        } catch (Exception e) {
            throw new InternalServerErrorException(ErrorCode.SIGNATURE_FAILED);
        }
    }
}
