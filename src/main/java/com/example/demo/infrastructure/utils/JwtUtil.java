package com.example.demo.infrastructure.utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.demo.common.exception.BadRequestException;
import com.example.demo.common.exception.InternalServerErrorException;
import com.example.demo.common.exception.UnauthorizedException;
import com.example.demo.common.exception.payload.ErrorCode;
import com.example.demo.common.model.TokenPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtUtil {
    
    private final String privateKeyValue = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCnZLCgycFjOc4eMYHzEE9H9vCHhIr+sqY0ZBhm8BsljA9J7Ng2QXE/MlYpkRuGYrH4mHvbv/fL3Pakm1v01wH9x9GFoBK7gngl9OvUuQQEcupWZuNJ891vxy+EYshQ6r13qaA5fTg1ULNBPkMqSmXsbeV5kWFgX2jKOCC4xEIYqdechA/a9tuSoJ7Z8TngmADyYdfHTqBI/ELI8Aryo8K4EegAWQBioZHRK3vMLYUs7Grj0BqJogKXNXYICF49V4awXKq8N4H9tdnP4Y3k2DnVWQIZ3WGI4MG4r6/rcLZqUTXk1m1XUAyDV3u6LtV2tglrn5fPzFBAAZILPQdCPyGPAgMBAAECggEAAKfuuzXG+EPORS3oFqnY/BcR5+Ui4HrfuApFLd40yV8GXtPhNkoE6Gw8crgq4udViiCTMWYKIi/1+hvnz2fZyKH9WMDam/8Mz0IvK3BFJbtQ3rHBJ7LWPc1bBzN40L/oAeN/6i+0Cdu8KQOqZHW4hZ5NF0JigYHACjUencPkKCIffiaC6MpX2iV2iD2/OstWayXFMjXcSwkB6H6Ft+ucjm19t1PtJOOXtMpm1DVG+mr9RE2s7C4TaSXx/8v9H0O+sIKlWew4/NfaIPx4lxQrHtpU1XIWCjF2T0vXGGBKkKc+/CBYG7NpdmdZcKxwHPJWA8LYHl0+N3Usx1b9tfbhsQKBgQDUWzdXZWHbT60oTpvxpPSCUyNByUmB8H0ojXtU0CJGyi6pkGKpCnHWpBgmulFXk0JYmMvLLB58l7qkNQsPVsduSMZgnpEMyd75orofOHYKAcJNNa+0x20GSI60nGtp0NLR8sPiAG+YkdwQa6poJXHyHVV+g7e44wS8GQqqgjUruQKBgQDJy9GS2cbTZyW1JtYbzpE6BCENVu7F6FvfhUWt+cPK8tMCf4bNO7adFxiNkBaTAWPB1QchzNc7szpH9PLhUPOXMjlxVaNUNwSVlFzpU5kYc8oq7qc5emFRQHnYbwt7Gr//PfR22K3pCMt1iU5btESV0+mPZGlWC5o0TCLKaUUrhwKBgA0hJqbMqeQ5ZOddFN8357Y3FbL3kwfpqpNbTAjOZZAi6Aer2a3B64/tzWB3nJfhjvSTXeZN8AScDPoiHXDxWJtE/J70rtsvkA23NfLANuwfd7f2Xcq7mc4NpmUcVrfQJXi9ncbXaA7y1bxeK8cMqaLilWqhEjboMl6+3Z31NvxhAoGAVAg/JnLvu/r5Fd2tTXkQuuMbTG3GEr32/DaIVLFsmifSPPkUDoTuP65teA3niKIMg5wNU0L04hR5kZtsRO2r8mtkxlBOKnSawRs+MZ+xYiBdsNEe6+2NdwNtefaMBmCWUVjgY1lJ2n/iEfjym3ePFAFiAmdjAgTk2yBVYIrD1iMCgYA9FlpN+lMj5+YhpGS9PRXmTt6DKXDTSVK+OhFfmOuTO7//4KPqDQau4EalzebHOZR9jYm8nIyZEsmQdA76Na8XmNGqIKvEr7UUWMOrwoqPARYAvK5enlCZIDxbGeqVwj6GfhGeZaTyfmVbFoxEG6SXtZhxfcpmsof4NDBxUFPrAA==";
    private final String KID = "kid-fixed-key";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String createToken(String userId, long expirationTime, String issuer, String audience, String tokenType) {
        try {
            long expiration = System.currentTimeMillis() + expirationTime;
            TokenPayload tokenPayload = new TokenPayload(userId, expiration, issuer, audience, tokenType);
            String header = createHeader("RS256", "JWT", KID);
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
            String header = createHeader("RS256", "JWT", KID);
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

//            String header = parts[0];
            String payload = parts[1];
//            String signature = parts[2];

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

//            String dataToSign = header + "." + payload;

            return true;
        } catch (BadRequestException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_VALIDATION_FAILED);
        }
    }
    
    public String getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if(parts.length != 3) {
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
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyValue);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            
            System.out.println("date value: " + data);

            byte[] signedBytes = signature.sign();
            System.out.println("Generated Signature Length: " + signedBytes.length);
            
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signedBytes);
        } catch (Exception e) {
            throw new InternalServerErrorException(ErrorCode.SIGNATURE_FAILED);
        }
    }
}