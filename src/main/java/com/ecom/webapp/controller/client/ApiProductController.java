package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.Category;
import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.Store;
import com.ecom.webapp.model.dto.ProductDTO;
import com.ecom.webapp.model.responseDto.ProductResponse;
import com.ecom.webapp.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiProductController {

    @Autowired
    private ProductService productService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.productService.getProducts(params), HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable(value = "id") int id) {
        return new ResponseEntity<>(this.productService.getProductById(id), HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable(value = "id") int id) {
        this.productService.deleteProduct(id);
    }

    @PostMapping("/products/add")
    public ResponseEntity<?> addProduct(@Valid @ModelAttribute ProductDTO request, BindingResult result) throws MethodArgumentNotValidException {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null, result);
        }
        Product product = new Product();
        product.setName(request.getName());
        product.setManufacturer(request.getManufacturer());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());

        Category category = new Category();
        category.setId(request.getCategoryId());
        product.setCategory(category);

        Store store = new Store();
        store.setId(request.getStoreId());
        product.setStore(store);

        if (request.getFile() != null && !request.getFile().isEmpty()) {
            product.setFile(request.getFile());
        }

        Product newPro = productService.addOrUpdate(product);
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(newPro, response);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
