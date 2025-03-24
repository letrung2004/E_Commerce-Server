package com.ecom.webapp.service;

import com.ecom.webapp.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Product> getProducts(Map<String, String> params);
}
