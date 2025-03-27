package com.ecom.webapp.service;

import com.ecom.webapp.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();
    Category addOrUpdate(Category c);
    Category getCategoryById(int id);
    void deleteCategory(int id);
}
