package com.example.demo.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class SessionService {
	
	public void createSession(HttpSession  session, String userId) {
		session.setAttribute("userId", userId);
		session.setMaxInactiveInterval(3600);
	}
	
	public boolean isSessionValid(HttpSession session) {
		return session.getAttribute("userId") != null;
	}
	
	public void invalidateSession(HttpSession session) {
		session.invalidate();
	}

}