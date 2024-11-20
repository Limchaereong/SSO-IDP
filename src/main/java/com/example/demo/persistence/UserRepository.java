package com.example.demo.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.demo.common.model.User;

public class UserRepository {
	
	private final Map<String, User> users = new HashMap<>();
	
	public UserRepository() {
		users.put("user1", new User("user1", "1111"));
	}
	
	public Optional<User> findByUsername(String username) {
		return Optional.ofNullable(users.get(username));
	}

}
