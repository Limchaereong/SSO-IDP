package com.example.demo.presentation.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.exception.NotFoundException;
import com.example.demo.common.exception.payload.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/jwks")
public class JWKSController {

    @GetMapping("/jwks.json")
    public Map<String, Object> getJWKS() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File jwksFile = new File("src/main/resources/jwks.json");
            return objectMapper.readValue(jwksFile, Map.class);
        } catch (IOException e) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_JWKS_FILE);
        }
    }
}