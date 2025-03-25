package com.ecom.webapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryDto {

    private Integer id;

    @Size(max = 50, message = "Tên danh mục không được quá 50 ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String name;
}
