package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.Category;
import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.dto.CategoryDto;
import com.ecom.webapp.model.dto.ProductDTO;
import com.ecom.webapp.model.dto.StoreDto;
import com.ecom.webapp.model.responseDto.ProductResponse;
import com.ecom.webapp.repository.CommentRepository;
import com.ecom.webapp.repository.ReviewRepository;
import com.ecom.webapp.repository.StoreRepository;
import com.ecom.webapp.service.CategoryService;
import com.ecom.webapp.service.ProductService;
import com.ecom.webapp.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
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
    @Autowired
    private ProductService productService;
    @Autowired
    private StoreRepository storeRepository;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @PostMapping("secure/store-activation")
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

    @PostMapping("secure/store/{storeId}/categories")
    public ResponseEntity<Category> addStoreCategory(@PathVariable(value = "storeId") int storeId,
                                                     @Valid @RequestBody CategoryDto categoryDto) {
        Category newCategory = this.categoryService.addCategory(categoryDto, storeId);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @DeleteMapping("secure/store/{storeId}/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(value = "id") int id,
                               @PathVariable(value = "storeId") int storeId) {
        this.categoryService.deleteCategory(id);
    }

    @PutMapping("secure/store/{storeId}/categories/{categoryId}")
    public ResponseEntity<Category> updateStoreCategory(@PathVariable(value = "storeId") int storeId,
                                                        @PathVariable(value = "categoryId") int categoryId,
                                                        @Valid @RequestBody CategoryDto categoryDto) {
        Category updatedCategory = this.categoryService.updateCategory(categoryId, storeId, categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }


    @GetMapping("/store/{storeId}/products")
    public ResponseEntity<List<ProductDTO>> getStoreProducts(@PathVariable(value = "storeId") int storeId,
                                                             @RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.productService.getProductsByStore(storeId, params), HttpStatus.OK);
    }

    @PostMapping("secure/store/{storeId}/products")
    public ResponseEntity<?> addStoreProduct(@PathVariable(value = "storeId") int storeId,
                                             @Valid @ModelAttribute ProductDTO productDTO, BindingResult result) throws MethodArgumentNotValidException {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null, result);
        }
        Product newPro = productService.addProduct(productDTO, storeId);
        ProductResponse productResponse = new ProductResponse(newPro);
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }


    @PutMapping("secure/store/{storeId}/products/{productId}")
    public ResponseEntity<?> updateStoreProduct(@PathVariable(value = "storeId") int storeId,
                                                @PathVariable(value = "productId") int productId,
                                                @ModelAttribute ProductDTO productDTO) {
        Product updateProduct = productService.updateProduct(productDTO, storeId, productId);
        ProductResponse productResponse = new ProductResponse(updateProduct);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("secure/store/{storeId}/products/{productId}")
    public ResponseEntity<ProductDTO> getStoreProductDetail(@PathVariable(value = "storeId") int storeId,
                                                            @PathVariable(value = "productId") int productId){
        return new ResponseEntity<>(this.productService.getProductById(productId), HttpStatus.OK);
    }


    @DeleteMapping("secure/store/{storeId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable(value = "productId") int productId,
                              @PathVariable(value = "storeId") int storeId) {
        this.productService.deleteProduct(productId);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getStoreDetail(@PathVariable(value = "storeId") int storeId){
        return new ResponseEntity<>(this.storeService.getStoreById(storeId), HttpStatus.OK);
    }

}
