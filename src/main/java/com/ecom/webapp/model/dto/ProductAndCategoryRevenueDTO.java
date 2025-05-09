package com.ecom.webapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAndCategoryRevenueDTO {
    private Integer id;
    private String name;
    private String period;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private Long quantitySold = 0L; // số lượng bán ra
    private Long orderCount = 0L; //số đơn hàng

}
