package com.example.demo.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.common.model.User;

@Repository
public class UserRepository {
    
    private final Map<String, User> users = new HashMap<>();
    
    public UserRepository() {
        users.put("user1", new User("1", "user1", "1111", "sa990422@gmail.com", "010-6615-7835"));
    }
    
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public Optional<User> findByUserId(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public Optional<String> findEmailByUserId(String userId) {
        return users.values().stream()
                .filter(user -> user.userId().equals(userId))
                .map(User::email)
                .findFirst();
    }

    public Optional<String> findPhoneNumberByUserId(String userId) {
        return users.values().stream()
                .filter(user -> user.userId().equals(userId))
                .map(User::phoneNumber)
                .findFirst();
    }
}