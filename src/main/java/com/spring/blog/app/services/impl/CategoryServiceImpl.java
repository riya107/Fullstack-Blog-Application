package com.spring.blog.app.services.impl;

import com.spring.blog.app.domain.entities.Category;
import com.spring.blog.app.repositories.CategoryRepo;
import com.spring.blog.app.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;


    @Override
    public List<Category> listCategories() {
        return categoryRepo.findAllWithPostCount();
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {

        String categoryName = category.getName();

        if (categoryRepo.existsByNameIgnoreCase(category.getName()))
        {
            throw new IllegalArgumentException("Category already exists with name: " + categoryName);
        }
        return categoryRepo.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        Optional<Category> category = categoryRepo.findById(id);
        if(category.isPresent())
        {
            if (!category.get().getPosts().isEmpty()){
                throw new IllegalStateException("Category has posts associated with it");
            }
        }
        categoryRepo.deleteById(id);
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }
}
