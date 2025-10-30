package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.payload.CategoryRequest;
import com.ecommerce.backend.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    @Transactional
    public Category addCategory(CategoryRequest req) {
        Category category = Category.builder()
                .name(req.getName())
                .description(req.getDescription())
                .build();
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, CategoryRequest req) {
        Category category = getCategoryById(id);
        category.setName(req.getName());
        category.setDescription(req.getDescription());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
