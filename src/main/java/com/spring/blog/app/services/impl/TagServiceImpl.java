package com.spring.blog.app.services.impl;

import com.spring.blog.app.domain.entities.Tag;
import com.spring.blog.app.mappers.TagMapper;
import com.spring.blog.app.repositories.TagRepo;
import com.spring.blog.app.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepo tagRepo;
    private final TagMapper tagMapper;

    @Override
    public List<Tag> getTags() {
        return tagRepo.findAllWithPostCount();
    }

    @Transactional
    @Override
    public List<Tag> createTags(Set<String> tagNames) {
        List<Tag> existingTags = tagRepo.findByNameIn(tagNames);
        Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        List<Tag> newTags = tagNames.stream().filter(name -> !existingTagNames.contains(name))
                .map(name -> Tag.builder()
                        .name(name)
                        .posts(new HashSet<>())
                        .build()).toList();

        List<Tag> savedTags = new ArrayList<>();
        if (!newTags.isEmpty()){
            savedTags = tagRepo.saveAll(newTags);
        }

        savedTags.addAll(existingTags);
        return savedTags;
    }
    @Transactional
    @Override
    public void deleteTag(UUID id) {
        tagRepo.findById(id).ifPresent(tag -> {
            if (!tag.getPosts().isEmpty()){
                throw new IllegalStateException("Cannot delete tag with posts");
            }
            tagRepo.deleteById(id);
        });
    }

    @Override
    public Tag getTagById(UUID id) {
        return tagRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Tag not found with ID: " + id));
    }

    @Override
    public List<Tag> getTagByIds(Set<UUID> ids) {
        List<Tag> foundTags = tagRepo.findAllById(ids);
        if (foundTags.size() != ids.size())
            throw new EntityNotFoundException("Not all specified tag IDs exist");
        return foundTags;
    }

}
