package com.ecom.webapp.controller.client;


import com.ecom.webapp.model.dto.ProductDTO;
import com.ecom.webapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiProductController {

    @Autowired
    private ProductService productService;


    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.productService.getProducts(params), HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable(value = "id") int id) {
        return new ResponseEntity<>(this.productService.getProductById(id), HttpStatus.OK);
    }


}
