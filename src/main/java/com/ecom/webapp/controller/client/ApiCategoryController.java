package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.Category;
import com.ecom.webapp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiCategoryController {
    @Autowired
    CategoryService categoryService;


    @GetMapping("/categories")
    public ResponseEntity<List<Category>> list() {
        return new ResponseEntity<>(this.categoryService.getCategories(), HttpStatus.OK);
    }

    @GetMapping("categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable(value = "id") int id) {
        Category category = this.categoryService.getCategoryById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }
}
