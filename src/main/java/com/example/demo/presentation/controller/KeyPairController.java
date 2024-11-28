package com.example.demo.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.infrastructure.keyPair.KeyPairProvider;

@RestController
public class KeyPairController {

    private final KeyPairProvider keyPairProvider;

    public KeyPairController(KeyPairProvider keyPairProvider) {
        this.keyPairProvider = keyPairProvider;
    }

    @GetMapping("/api/publicKey")
    public String getPublicKey() {
        return keyPairProvider.getEncodedPublicKey();
    }
}