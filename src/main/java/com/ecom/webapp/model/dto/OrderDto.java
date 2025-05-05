package com.ecom.webapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class OrderDto {
    private int Id;
    private String username;

    @NotNull(message = "Không được bỏ trống mục này!")
    private int addressId;

    @NotNull(message = "Không được bỏ trống mục này!")
    private Long total;


    @Size(max = 45)
    @NotBlank(message = "Không được bỏ trống mục này!")
    private String paymentMethod;

    private Set<Integer> subCartItemIds;


    private Set<OrderItemDto> subOrderItems;
    private String transactionId;

}
