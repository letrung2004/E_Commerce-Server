package com.ecom.webapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class OrderDto {
    private int Id;

    @Size(max = 45, min=1, message = "Độ dài username không hợp lệ!")
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String username;

    @NotNull(message = "Không được bỏ trống mục này!")
    private int addressId;

    @NotNull(message = "Không được bỏ trống mục này!")
    private Long shippingFree;

//    @NotBlank(message = "Không được bỏ trống mục này!")
//    private BigDecimal total;


    @Size(max = 45)
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String paymentMethod;

    private Set<Integer> subCartItemIds;


    private Set<OrderItemDto> subOrderItems;

}
