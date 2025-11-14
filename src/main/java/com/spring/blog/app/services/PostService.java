package com.spring.blog.app.services;


import com.spring.blog.app.domain.CreatePostRequest;
import com.spring.blog.app.domain.UpdatePostRequest;
import com.spring.blog.app.domain.entities.Post;
import com.spring.blog.app.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface PostService {
    List<Post> getAllPosts(UUID categoryId, UUID tagId);
    List<Post> getDraftPosts(User user);
    Post createPost (User user, CreatePostRequest createPostRequest);
    Post updatePost (UUID id, UpdatePostRequest updatePostRequest);

    Post getPost(UUID id);

    public void deletePost(UUID id);
}
