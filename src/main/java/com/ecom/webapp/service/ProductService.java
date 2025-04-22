package com.ecom.webapp.service;

import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.dto.ProductDTO;

import java.util.List;
import java.util.Map;

public interface ProductService {
    List<ProductDTO> getProducts(Map<String, String> params);
    ProductDTO getProductById(int id);
    void deleteProduct(int id);
    List<ProductDTO> getProductsByStore(int storeId, Map<String, String> params);
    Product addProduct(ProductDTO productDto, int storeId);
    Product updateProduct(ProductDTO productDto, int storeId, int productId);
}
