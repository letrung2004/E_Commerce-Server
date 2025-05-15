package com.ecom.webapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorePerformanceDTO {
    private Long storeId;
    private String storeName;
    private Long productCount;
    private Long orderCount;
    private Long productsSold;
    private BigDecimal totalRevenue;
    private Double growthPercentage;
}