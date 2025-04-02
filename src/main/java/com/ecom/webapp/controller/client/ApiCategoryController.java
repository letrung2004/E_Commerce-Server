package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.Category;
import com.ecom.webapp.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiCategoryController {
    @Autowired
    CategoryService categoryService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> list() {
        return new ResponseEntity<>(this.categoryService.getCategories(), HttpStatus.OK);
    }

    @GetMapping("categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable(value = "id") int id) {
        Category category = this.categoryService.getCategoryById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping("categories/add")
    @CrossOrigin
    public ResponseEntity<Category> addCategory(@Valid @RequestBody Category category) {
        Category newCategory = this.categoryService.addOrUpdate(category);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED) ;
    }

    @DeleteMapping("categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(value = "id") int id) {
        this.categoryService.deleteCategory(id);
    }
}
