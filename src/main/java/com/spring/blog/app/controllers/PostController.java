package com.spring.blog.app.controllers;

import com.spring.blog.app.domain.CreatePostRequest;
import com.spring.blog.app.domain.UpdatePostRequest;
import com.spring.blog.app.domain.dtos.CreatePostRequestDto;
import com.spring.blog.app.domain.dtos.PostDto;
import com.spring.blog.app.domain.dtos.UpdatePostRequestDto;
import com.spring.blog.app.domain.entities.Post;
import com.spring.blog.app.domain.entities.User;
import com.spring.blog.app.mappers.PostMapper;
import com.spring.blog.app.repositories.PostRepo;
import com.spring.blog.app.services.PostService;
import com.spring.blog.app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final PostRepo postRepo;
    private final UserService userService;
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(
          @RequestParam(required = false) UUID categoryId,
          @RequestParam(required = false) UUID tagId) {
        List<Post> posts = postService.getAllPosts(categoryId, tagId);
        List<PostDto> postDtos = posts.stream().map(postMapper::toDto).toList();
        return ResponseEntity.ok(postDtos);

    }

    @GetMapping(path = "/drafts")
    public ResponseEntity<List<PostDto>> getDrafts(@RequestAttribute UUID userId) {
        User loggedInUser = userService.getUserById(userId);
        List<Post> draftPosts = postService.getDraftPosts(loggedInUser);
        List<PostDto> postDtos = draftPosts.stream().map(postMapper::toDto).toList();
        return ResponseEntity.ok(postDtos);

    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @RequestBody CreatePostRequestDto createPostRequestDto,
            @RequestAttribute UUID userId
            ) {
        User loggedInUser = userService.getUserById(userId);
        CreatePostRequest createPostRequest = postMapper.toCreatePostRequest(createPostRequestDto);
        Post createdPost = postService.createPost(loggedInUser, createPostRequest);
        PostDto createdPostDto = postMapper.toDto(createdPost);
        return  new ResponseEntity<>(createdPostDto, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable UUID id, @RequestBody UpdatePostRequestDto updatePostRequestDto)
    {
        UpdatePostRequest updatePostRequest = postMapper.toUpdatePostRequest(updatePostRequestDto);
        Post updatePost = postService.updatePost(id, updatePostRequest);
        PostDto updatePostDto = postMapper.toDto(updatePost);
        return ResponseEntity.ok(updatePostDto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostDto> getPost( @PathVariable UUID id)
    {
        Post getPost = postService.getPost(id);
        PostDto postDto = postMapper.toDto(getPost);
        return ResponseEntity.ok(postDto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id )
    {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

}
