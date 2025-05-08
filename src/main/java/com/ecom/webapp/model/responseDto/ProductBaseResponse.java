package com.ecom.webapp.model.responseDto;

import com.ecom.webapp.model.Product;
import lombok.Data;


@Data
public class ProductBaseResponse {
    private int id;
    private String name;
    private String image;
    private String description;

    public ProductBaseResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.image = product.getImage();
        this.description = product.getDescription();
    }
}
