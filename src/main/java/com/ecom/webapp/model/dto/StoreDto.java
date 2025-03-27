package com.ecom.webapp.model.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StoreDto {

    @Size(max = 50, message = "Tên cửa hàng không được quá 50 ký tự!")
    @Size(min = 2, message = "Tên cửa hàng không được ít hơn 2 ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String name;

    @Size(max = 255)
    private String logo;

    @Size(max = 200, message = "Mô tả không được quá 200 ký tự!")
    private String description;

    @NotBlank(message = "Id người dùng không được bỏ trống!")
    private String username;
}
