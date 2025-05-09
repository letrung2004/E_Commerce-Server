package com.ecom.webapp.model.responseDto;


import com.ecom.webapp.model.Category;
import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.Store;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductResponse {
    private int id;
    private String name;
    private String image;
    private String productPrice;
    private String description;
    private Date dateCreated;
    private Integer categoryId;
    private Integer storeId;


    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.image = product.getImage();
        this.productPrice = product.getPrice().toString();
        this.description = product.getDescription();
        this.dateCreated = product.getDateCreated();
        this.categoryId = (product.getCategory() != null) ? product.getCategory().getId() : null;
        this.storeId = (product.getStore() != null) ? product.getStore().getId() : null;
    }
}
