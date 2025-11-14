package com.spring.blog.app.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    UserDetails authenticate(String email, String password);
    UserDetails register(String name, String email, String password);
    String generateToken(UserDetails userDetails);
    UserDetails validateToken(String token);
}