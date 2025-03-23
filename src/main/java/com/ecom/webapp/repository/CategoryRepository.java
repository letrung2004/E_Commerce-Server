package com.ecom.webapp.repository;

import com.ecom.webapp.model.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> getCategories();
}
