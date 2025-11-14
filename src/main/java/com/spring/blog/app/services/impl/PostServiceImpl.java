package com.spring.blog.app.services.impl;

import com.spring.blog.app.domain.CreatePostRequest;
import com.spring.blog.app.domain.PostStatus;
import com.spring.blog.app.domain.UpdatePostRequest;
import com.spring.blog.app.domain.entities.Category;
import com.spring.blog.app.domain.entities.Post;
import com.spring.blog.app.domain.entities.Tag;
import com.spring.blog.app.domain.entities.User;
import com.spring.blog.app.repositories.PostRepo;
import com.spring.blog.app.services.CategoryService;
import com.spring.blog.app.services.PostService;
import com.spring.blog.app.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.SecondaryTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepo postRepo;
    private final CategoryService categoryService;
    private final TagService tagService;
    private static final int WORD_PER_MINUTE = 200;


    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPosts(UUID categoryId, UUID tagId) {
        if (categoryId != null && tagId != null){
            Category category = categoryService.getCategoryById(categoryId);
            Tag tag = tagService.getTagById(tagId);
            postRepo.findAllByStatusAndCategoryAndTagsContaining(
                    PostStatus.PUBLISHED,
                    category,
                    tag
            );
        }

        if (categoryId != null){
            Category category = categoryService.getCategoryById(categoryId);
            postRepo.findAllByStatusAndCategory(
                    PostStatus.PUBLISHED,
                    category
            );

        }

        if (tagId != null){
            Tag tag = tagService.getTagById(tagId);
            postRepo.findAllByStatusAndTagsContaining(
                    PostStatus.PUBLISHED,
                    tag
            );
        }
        return postRepo.findAllByStatus(PostStatus.PUBLISHED);

    }

    @Override
    public List<Post> getDraftPosts(User user) {
       return postRepo.findAllByAuthorAndStatus(user, PostStatus.DRAFT);

    }
    @Transactional
    @Override
    public Post createPost(User user, CreatePostRequest createPostRequest) {
        Post newPost = new Post();
        newPost.setTitle(createPostRequest.getTitle());
        newPost.setContent(createPostRequest.getContent());
        newPost.setStatus(createPostRequest.getStatus());
        newPost.setAuthor(user);
        newPost.setReadingTime(calculateReadingTime(createPostRequest.getContent()));
        Category category =  categoryService.getCategoryById(createPostRequest.getCategoryId());
        newPost.setCategory(category);

        Set<UUID> tagIds = createPostRequest.getTagIds();
        List<Tag> tags = tagService.getTagByIds(tagIds);
        newPost.setTags(new HashSet<>(tags));
        return postRepo.save(newPost);
    }
    @Transactional
    @Override
    public Post updatePost(UUID id, UpdatePostRequest updatePostRequest) {
        Post existingPost = postRepo.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Post does not exist with Id: " + id) );

        existingPost.setTitle(updatePostRequest.getTitle());
        String postContent = updatePostRequest.getContent();
        existingPost.setContent(postContent);
        existingPost.setStatus(updatePostRequest.getStatus());
        existingPost.setReadingTime(calculateReadingTime(postContent));

        Category category =  categoryService.getCategoryById(updatePostRequest.getCategoryId());
        existingPost.setCategory(category);
        UUID updatePostRequestCategoryId = updatePostRequest.getCategoryId();
        if (existingPost.getCategory().getId().equals(updatePostRequestCategoryId)){
            Category newCategory =  categoryService.getCategoryById(updatePostRequest.getCategoryId());
            existingPost.setCategory(newCategory);
        }

        Set<UUID> existingTagIds = updatePostRequest.getTagIds();
        Set<UUID> updatePostRequestTagIds = updatePostRequest.getTagIds();

        if (!existingTagIds.equals(updatePostRequestTagIds))
        {
            List<Tag> newTag = tagService.getTagByIds(updatePostRequestTagIds);
            existingPost.setTags(new HashSet<>(newTag));
        }
        return postRepo.save(existingPost);

    }

    @Override
    public Post getPost(UUID id) {
        return postRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Post does not exist with ID: " +id));
    }

    @Override
    public void deletePost(UUID id) {
        Post post = getPost(id);
        postRepo.delete(post);
    }

    private Integer calculateReadingTime (String content)
    {
        if (content == null || content.isEmpty()){
            return 0;
        }
        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil((double) wordCount / WORD_PER_MINUTE);
    }
}
