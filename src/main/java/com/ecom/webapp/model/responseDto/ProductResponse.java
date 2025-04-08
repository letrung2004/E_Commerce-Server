package com.ecom.webapp.model.responseDto;


import com.ecom.webapp.model.Category;
import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.Store;
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
    private BigDecimal starRate;
    private Byte active;
    private Category category;
    private Store store;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.image = product.getImage();
        this.productPrice = product.getPrice().toString();
        this.description = product.getDescription();
        this.dateCreated = product.getDateCreated();
        this.starRate = product.getStarRate();
        this.active = product.getActive();
        this.category = product.getCategory();
        this.store = product.getStore();
    }
    public ProductResponse() {}

}
