package com.example.demo.infrastructure.jwks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.io.File;
import java.io.IOException;

import com.example.demo.common.exception.NotFoundException;
import com.example.demo.common.exception.payload.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWKSProvider {
	
	private final Map<String, String> publicKeys;
	
	public JWKSProvider() {
		this.publicKeys = loadPublicKeysFromJWKS("src/main/resources/jwks.json");
	}
	
	private Map<String, String> loadPublicKeysFromJWKS(String jwksFilePath) {
		Map<String, String> keyMap = new HashMap<>();
		
		File file = new File(jwksFilePath);
		
		if(!file.exists()) {
			throw new NotFoundException(ErrorCode.NOT_FOUND_JWKS_FILE);
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jwks = mapper.readTree(new File(jwksFilePath));
			
			JsonNode keysNode = jwks.get("keys");
			
			if(keysNode.isEmpty()) {
				throw new NotFoundException(ErrorCode.NOT_FOUND_KEYS);
			}
			
			for(JsonNode key : jwks.get("keys")) {
				String kid = key.get("kid").asText();
				String publicKey = key.get("n").asText();
				keyMap.put(kid, publicKey);
			}
		} catch (IOException e) {
			throw new NotFoundException(ErrorCode.JWKS_ERROR);
		}
		
		return keyMap;
		
	}
	
	public Optional<String> getPublicKey(String kid) {
		return Optional.ofNullable(publicKeys.get(kid));
	}

}
