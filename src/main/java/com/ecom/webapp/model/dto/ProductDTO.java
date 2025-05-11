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

    @NotNull(message = "Vui lòng nhập tên sản phẩm")
    @Size(max = 255,min = 2, message = "Tên sản phẩm phải ít nhất từ 3 ký tự")
    private String name;

    @NotNull(message = "Vui lòng nhập tên nhà sản xuất")
    @Size(max = 100, message = "Tên nhà sản xuất không được vượt quá 100 ký tự")
    private String manufacturer;

    @NotNull(message = "Vui lòng nhập giá sản phẩm")
    @DecimalMin(value = "0.01", message = "Giá sản phẩm phải lớn hơn 0")
    private BigDecimal price;

    @Size(max = 255, message = "Mô tả sản phẩm không được vượt quá 250 ký tự")
    private String description;
    private String image;
    private Date dateCreated;
    private BigDecimal starRate;
    private Byte active;

    @NotNull(message = "Vui lòng chọn danh mục")
    private Integer categoryId;

//    @NotNull(message = "Vui lòng chọn cửa hàng")
    private Integer storeId;

    private MultipartFile file;
    private String storeName;
    private String matchReason;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.image = product.getImage();
        this.manufacturer = product.getManufacturer();
    }
}