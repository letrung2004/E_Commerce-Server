package com.ecom.webapp.service;

import com.ecom.webapp.model.Category;
import com.ecom.webapp.model.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();
    Category addCategory(CategoryDto categoryDto, int storeId);
    Category getCategoryById(int id);
    void deleteCategory(int id);
    List<Category> getCategoriesByStoreId(int storeId);
    Category updateCategory(int categoryId, int storeId, CategoryDto categoryDto);

}
