package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.dto.ProductDTO;
import com.ecom.webapp.repository.ProductRepository;
import com.ecom.webapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;


    private ProductDTO convertProductToProductDTO(Product p) {
        if (p == null) return null;
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setManufacturer(p.getManufacturer());
        dto.setPrice(p.getPrice());
        dto.setDescription(p.getDescription());
        dto.setImage(p.getImage());
        dto.setDateCreated(p.getDateCreated());
        dto.setStarRate(p.getStarRate());
        dto.setActive(p.getActive());
        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
        }
        if (p.getStore() != null) {
            dto.setStoreId(p.getStore().getId());
        }
        return dto;
    }

    @Override
    public List<ProductDTO> getProducts(Map<String, String> params) {
        List<Product> products = this.productRepository.getProducts(params);
        return products.stream().map(this::convertProductToProductDTO).collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(int id) {
        Product product = this.productRepository.getProductById(id);
        return convertProductToProductDTO(product);
    }

    @Override
    public Product addOrUpdate(Product p) {
        System.out.println(new Date());
        p.setDateCreated(new Date());
        this.productRepository.addOrUpdate(p);
        return p;
    }

    @Override
    public void deleteProduct(int id) {
        this.productRepository.deleteProduct(id);
    }
}
