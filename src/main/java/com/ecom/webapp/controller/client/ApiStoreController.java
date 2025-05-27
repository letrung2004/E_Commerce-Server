package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.Category;
import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.dto.CategoryDto;
import com.ecom.webapp.model.dto.ProductDTO;
import com.ecom.webapp.model.dto.StoreDto;
import com.ecom.webapp.model.responseDto.OrderResponse;
import com.ecom.webapp.model.responseDto.ProductResponse;
import com.ecom.webapp.service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiStoreController {

    @Autowired
    private StoreService storeService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // method validate authentication
    private ResponseEntity<String> validateAuthentication(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }
        return null;
    }

    // method get store ID từ username
    private Optional<Integer> getStoreIdByPrincipal(Principal principal) {
        try {
            String username = principal.getName();
            int storeId = this.storeService.getStoreIdByUsername(username);
            return Optional.of(storeId);
        } catch (EntityNotFoundException ex) {
            return Optional.empty();
        }
    }

    // method validate authentication và get store ID
    private ResponseEntity<?> validateAndGetStoreId(Principal principal) {
        ResponseEntity<String> authError = validateAuthentication(principal);
        if (authError != null) {
            return authError;
        }

        Optional<Integer> storeIdOpt = getStoreIdByPrincipal(principal);
        if (storeIdOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ban hien khong co cua hang de thuc hien thao tac tren !!!");
        }

        return null;
    }

    @PostMapping("/secure/store-activation")
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

    @PostMapping("/secure/store/categories")
    public ResponseEntity<?> addStoreCategory(@Valid @RequestBody CategoryDto categoryDto,
                                              Principal principal) {
        ResponseEntity<?> validationError = validateAndGetStoreId(principal);
        if (validationError != null) {
            return validationError;
        }

        int storeId = getStoreIdByPrincipal(principal).get();
        Category newCategory = this.categoryService.addCategory(categoryDto, storeId);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @DeleteMapping("/secure/store/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable(value = "id") int id, Principal principal) {
        ResponseEntity<?> validationError = validateAndGetStoreId(principal);
        if (validationError != null) {
            return validationError;
        }

        this.categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/secure/store/categories/{categoryId}")
    public ResponseEntity<?> updateStoreCategory(Principal principal,
                                                 @PathVariable(value = "categoryId") int categoryId,
                                                 @Valid @RequestBody CategoryDto categoryDto) {
        ResponseEntity<?> validationError = validateAndGetStoreId(principal);
        if (validationError != null) {
            return validationError;
        }

        int storeId = getStoreIdByPrincipal(principal).get();
        Category updatedCategory = this.categoryService.updateCategory(categoryId, storeId, categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @GetMapping("/store/{storeId}/products")
    public ResponseEntity<List<ProductDTO>> getStoreProducts(@PathVariable(value = "storeId") int storeId,
                                                             @RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.productService.getProductsByStore(storeId, params), HttpStatus.OK);
    }

    @PostMapping("/secure/store/products")
    public ResponseEntity<?> addStoreProduct(Principal principal,
                                             @Valid @ModelAttribute ProductDTO productDTO, BindingResult result)
            throws MethodArgumentNotValidException {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null, result);
        }

        ResponseEntity<?> validationError = validateAndGetStoreId(principal);
        if (validationError != null) {
            return validationError;
        }

        int storeId = getStoreIdByPrincipal(principal).get();
        Product newPro = productService.addProduct(productDTO, storeId);
        ProductResponse productResponse = new ProductResponse(newPro);
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @PutMapping("/secure/store/products/{productId}")
    public ResponseEntity<?> updateStoreProduct(Principal principal,
                                                @PathVariable(value = "productId") int productId,
                                                @Valid @ModelAttribute ProductDTO productDTO, BindingResult result)
            throws MethodArgumentNotValidException {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null, result);
        }

        ResponseEntity<?> validationError = validateAndGetStoreId(principal);
        if (validationError != null) {
            return validationError;
        }

        int storeId = getStoreIdByPrincipal(principal).get();
        Product updateProduct = productService.updateProduct(productDTO, storeId, productId);
        ProductResponse productResponse = new ProductResponse(updateProduct);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/secure/store/products/{productId}")
    public ResponseEntity<?> getStoreProductDetail(Principal principal,
                                                   @PathVariable(value = "productId") int productId) {
        ResponseEntity<?> validationError = validateAndGetStoreId(principal);
        if (validationError != null) {
            return validationError;
        }

        return new ResponseEntity<>(this.productService.getProductById(productId), HttpStatus.OK);
    }

    @DeleteMapping("/secure/store/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(value = "productId") int productId,
                                           Principal principal) {
        ResponseEntity<?> validationError = validateAndGetStoreId(principal);
        if (validationError != null) {
            return validationError;
        }

        this.productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getStoreDetail(@PathVariable(value = "storeId") int storeId) {
        return new ResponseEntity<>(this.storeService.getStoreById(storeId), HttpStatus.OK);
    }

    @PostMapping("/secure/store/products/{productId}/update-status")
    public ResponseEntity<?> updateStatusProduct(@PathVariable(value = "productId") int productId,
                                                 Principal principal) {
        ResponseEntity<?> validationError = validateAndGetStoreId(principal);
        if (validationError != null) {
            return validationError;
        }

        this.productService.changeProductStatus(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stores")
    public ResponseEntity<?> getStores(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.storeService.getStores(params), HttpStatus.OK);
    }

    @GetMapping("/secure/store/{storeId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @PathVariable("storeId") int storeId,
            @RequestParam(value = "status", required = false, defaultValue = "") String status,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page) {

        List<OrderResponse> orders = this.orderService.getOrdersByStoreId(storeId, status, page);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}