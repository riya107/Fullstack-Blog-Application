package com.spring.blog.app.services;

import com.spring.blog.app.domain.entities.User;

import java.util.UUID;

public interface UserService {

    User getUserById(UUID id);

}
