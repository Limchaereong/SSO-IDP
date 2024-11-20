package com.example.demo.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.infrastructure.jwks.JWKSProvider;

@Configuration
public class JWKSConfig {
	
	@Bean
	public JWKSProvider jwksProvider() {
		return new JWKSProvider();
	}

}
