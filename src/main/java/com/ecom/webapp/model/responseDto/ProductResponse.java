package com.ecom.webapp.model.responseDto;


import com.ecom.webapp.model.Product;
import lombok.Data;

@Data
public class ProductResponse {
    private int productId;
    private String productName;
    private String productImage;
    private String productPrice;

    public ProductResponse(Product product) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.productImage = product.getImage();
        this.productPrice = product.getPrice().toString();
    }

}
