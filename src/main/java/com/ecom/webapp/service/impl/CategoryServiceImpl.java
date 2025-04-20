package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.Category;
import com.ecom.webapp.model.dto.CategoryDto;
import com.ecom.webapp.repository.CategoryRepository;
import com.ecom.webapp.repository.StoreRepository;
import com.ecom.webapp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Override
    @Transactional
    public List<Category> getCategories() {
        return categoryRepository.getCategories();
    }

    @Override
    @Transactional
    public Category addCategory(CategoryDto categoryDto, int storeId) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setStore(this.storeRepository.getStoreById(storeId));
        this.categoryRepository.addOrUpdateCategory(category);
        return category;
    }


    @Override
    public Category getCategoryById(int id) {
        return this.categoryRepository.getCategoryById(id);

    }

    @Override
    public void deleteCategory(int id) {
        Category category = this.categoryRepository.getCategoryById(id);
        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        this.categoryRepository.deleteCategory(id);
    }

    @Override
    public List<Category> getCategoriesByStoreId(int storeId) {
        return this.categoryRepository.getCategoriesByStoreId(storeId);
    }

    @Override
    public Category updateCategory(int categoryId, int storeId, CategoryDto categoryDto) {
        Category category = this.categoryRepository.getCategoryById(categoryId);
        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        category.setName(categoryDto.getName());
        this.categoryRepository.addOrUpdateCategory(category);
        return category;
    }
}
