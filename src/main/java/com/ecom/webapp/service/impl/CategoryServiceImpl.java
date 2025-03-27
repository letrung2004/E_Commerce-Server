package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.Category;
import com.ecom.webapp.repository.CategoryRepository;
import com.ecom.webapp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public List<Category> getCategories() {
        return categoryRepository.getCategories();
    }

    @Override
    public Category addOrUpdate(Category c) {
        this.categoryRepository.addOrUpdateCategory(c);
        return c;
    }

    @Override
    public Category getCategoryById(int id) {
        return this.categoryRepository.getCategoryById(id);

    }

    @Override
    public void deleteCategory(int id) {
        this.categoryRepository.deleteCategory(id);
    }
}
