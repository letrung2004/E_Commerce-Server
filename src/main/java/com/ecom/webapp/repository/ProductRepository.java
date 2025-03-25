package com.ecom.webapp.repository;

import com.ecom.webapp.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductRepository {
    List<Product> getProducts(Map<String, String> params);
    void addOrUpdate(Product p);
    Product getProductById(int id);
    void deleteProduct(int id);
}
