package com.ecom.webapp.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class OrderItemDto {
    int storeId;
    Set<Integer> subCartItemIds;
    Long shippingCost;
}
