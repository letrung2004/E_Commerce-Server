package com.ecom.webapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressDto {

    private int id;
    @Size(max = 50, message = "Tên cửa hàng không được quá 50 ký tự!")
    @Size(min = 2, message = "Tên cửa hàng không được ít hơn 2 ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String address;

    @Size(max = 50, message = "Tên người nhận không được quá 50 ký tự!")
    @Size(min = 2, message = "Tên người nhận không được ít hơn 2 ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String receiver;

    @Size(max = 10, min=10, message = "Số điện thoại người nhận phải đủ ký tự!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String phoneNumber;

    @Size(max = 45, min=1, message = "Độ dài username không hợp lệ!")
//    @NotBlank(message = "Không được bỏ trống mục này!")
    private String username;

    private Boolean defaultAddress;
}
