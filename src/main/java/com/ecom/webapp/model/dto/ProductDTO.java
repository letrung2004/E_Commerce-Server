package com.ecom.webapp.model.dto;

import com.ecom.webapp.model.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {
    private Integer id;

    @NotNull(message = "Product name is required")
    @Size(max = 255,min = 2, message = "Product name must be less than 255 and more than 2 characters")
    private String name;

    @NotNull(message = "Manufacturer is required")
    @Size(max = 100, message = "Manufacturer name must be less than 100 characters")
    private String manufacturer;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
    private String image;
    private Date dateCreated;
    private BigDecimal starRate;
    private Byte active;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

//    @NotNull(message = "Store ID is required")
    private Integer storeId;

    private MultipartFile file;


    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.image = product.getImage();
        this.manufacturer = product.getManufacturer();
    }
}