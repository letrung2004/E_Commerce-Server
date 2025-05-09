package com.ecom.webapp.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RevenueDTO {
    private Object period;
    private BigDecimal totalRevenue;
    private Long orderCount;
    private BigDecimal averagePrice;

    public RevenueDTO() {}

    public RevenueDTO(Object period, BigDecimal totalRevenue, Long orderCount, BigDecimal averagePrice) {
        this.period = period;
        this.totalRevenue = totalRevenue;
        this.orderCount = orderCount;
        this.averagePrice = averagePrice;
    }
}
