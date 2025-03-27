package com.ecom.webapp.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class ProductDTO {
    private Integer id;
    private String name;
    private String manufacturer;
    private BigDecimal price;
    private String description;
    private String image;
    private Date dateCreated;
    private BigDecimal starRate;
    private Byte active;
    private Integer categoryId;
    private Integer storeId;

    // Constructor
    public ProductDTO(Integer id, String name, String manufacturer, BigDecimal price,
                      String description, String image, Date dateCreated, BigDecimal starRate,
                      Byte active, Integer categoryId, Integer storeId) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.description = description;
        this.image = image;
        this.dateCreated = dateCreated;
        this.starRate = starRate;
        this.active = active;
        this.categoryId = categoryId;
        this.storeId = storeId;
    }

    public ProductDTO() {

    }
}