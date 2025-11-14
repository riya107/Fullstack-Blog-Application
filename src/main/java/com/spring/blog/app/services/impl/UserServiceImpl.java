package com.spring.blog.app.services.impl;

import com.spring.blog.app.domain.entities.User;
import com.spring.blog.app.repositories.UserRepo;
import com.spring.blog.app.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepository;

    @Override
    public User getUserById(UUID id) {
     return userRepository
             .findById(id)
             .orElseThrow(() -> new EntityNotFoundException("User not found with Id: " + id));
    }
}
