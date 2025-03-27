package com.ecom.webapp.service;

import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.dto.ProductDTO;

import java.util.List;
import java.util.Map;

public interface ProductService {
    List<ProductDTO> getProducts(Map<String, String> params);
    ProductDTO getProductById(int id);
    Product addOrUpdate(Product p);
    void deleteProduct(int id);
}
