package com.ecom.webapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.dto.ProductDTO;
import com.ecom.webapp.repository.CategoryRepository;
import com.ecom.webapp.repository.ProductRepository;
import com.ecom.webapp.repository.StoreRepository;
import com.ecom.webapp.service.ProductService;
import jakarta.data.repository.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    protected ProductDTO convertProductToProductDTO(Product p) {
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
            dto.setStoreName(p.getStore().getName());
        }
        return dto;
    }

    @Override
    public List<ProductDTO> getProducts(Map<String, String> params) {
        List<Product> products = this.productRepository.getProducts(params);
        String kw = params.get("q") != null ? params.get("q").toLowerCase() : null;
        return products.stream()
                .map(p -> {
                    ProductDTO dto = convertProductToProductDTO(p);

                    if (kw != null && !kw.isEmpty()) {
                        boolean matchProduct = p.getName() != null && p.getName().toLowerCase().contains(kw);
                        boolean matchStore = p.getStore() != null && p.getStore().getName().toLowerCase().contains(kw);

                        if (matchProduct && matchStore)
                            dto.setMatchReason("both");
                        else if (matchProduct)
                            dto.setMatchReason("product");
                        else if (matchStore)
                            dto.setMatchReason("store");
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDTO getProductById(int id) {
        Product product = this.productRepository.getProductById(id);
        return convertProductToProductDTO(product);
    }


    @Override
    public void deleteProduct(int id) {
        Product product = this.productRepository.getProductById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        this.productRepository.deleteProduct(id);
    }

    @Override
    public List<ProductDTO> getProductsByStore(int storeId, Map<String, String> params) {
        List<Product> products = this.productRepository.getProductsByStore(storeId, params);
        return products.stream().map(this::convertProductToProductDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Product addProduct(ProductDTO productDto, int storeId) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setManufacturer(productDto.getManufacturer());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setStore(storeRepository.getStoreById(storeId));
        product.setDateCreated(new Date());
        product.setActive(Byte.valueOf("1"));
        product.setCategory(categoryRepository.getCategoryById(productDto.getCategoryId()));

        if (productDto.getFile() != null && !productDto.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(productDto.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                product.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(ProductServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.productRepository.addOrUpdate(product);
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(ProductDTO productDto, int storeId, int productId) {
        Product product = this.productRepository.getProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        if (productDto.getName() != null)
            product.setName(productDto.getName());

        if (productDto.getManufacturer() != null)
            product.setManufacturer(productDto.getManufacturer());

        if (productDto.getPrice() != null)
            product.setPrice(productDto.getPrice());

        if (productDto.getDescription() != null)
            product.setDescription(productDto.getDescription());

        if (productDto.getCategoryId() != null)
            product.setCategory(categoryRepository.getCategoryById(productDto.getCategoryId()));

        if (productDto.getFile() != null && !productDto.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(productDto.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                product.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(ProductServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.productRepository.addOrUpdate(product);
        return product;
    }

    @Override
    public void changeProductStatus(int productId) {
        this.productRepository.changStatus(productId);
    }

}
