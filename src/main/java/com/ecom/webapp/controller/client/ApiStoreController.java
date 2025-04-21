package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.Category;
import com.ecom.webapp.model.dto.CategoryDto;
import com.ecom.webapp.model.dto.StoreDto;
import com.ecom.webapp.repository.CommentRepository;
import com.ecom.webapp.repository.ReviewRepository;
import com.ecom.webapp.service.CategoryService;
import com.ecom.webapp.service.StoreService;
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
@RequestMapping("/api/secure")
@CrossOrigin
public class ApiStoreController {

    @Autowired
    private StoreService storeService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CategoryService categoryService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/store-activation")
    public ResponseEntity<StoreDto> requestActivationStore(@Valid @RequestBody StoreDto storeDto) {
        System.out.println(storeDto);
        this.storeService.createStore(storeDto);
        return new ResponseEntity<>(storeDto, HttpStatus.CREATED);
    }

    @GetMapping("/store/{storeId}/categories")
    public ResponseEntity<List<Category>> getStoreCategories(@PathVariable(value = "storeId") int storeId) {
        List<Category> categories = this.categoryService.getCategoriesByStoreId(storeId);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping("/store/{storeId}/categories")
    public ResponseEntity<Category> addStoreCategory(@PathVariable(value = "storeId") int storeId,
                                                     @Valid @RequestBody CategoryDto categoryDto) {
        Category newCategory = this.categoryService.addCategory(categoryDto, storeId);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @DeleteMapping("/store/{storeId}/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(value = "id") int id,
                               @PathVariable(value = "storeId") int storeId) {
        this.categoryService.deleteCategory(id);
    }

    @PutMapping("/store/{storeId}/categories/{categoryId}")
    public ResponseEntity<Category> updateStoreCategory(@PathVariable(value = "storeId") int storeId,
                                                        @PathVariable(value = "categoryId") int categoryId,
                                                        @Valid @RequestBody CategoryDto categoryDto) {
        Category updatedCategory = this.categoryService.updateCategory(categoryId,storeId,categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }





}
