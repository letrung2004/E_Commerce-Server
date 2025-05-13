package com.ecom.webapp.service;

import com.ecom.webapp.model.dto.ProductAndCategoryRevenueDTO;
import com.ecom.webapp.model.dto.RevenueDTO;

import java.util.List;

public interface RevenueService {
    List<RevenueDTO> getRevenueByPeriod(int storeId, String period);
    List<ProductAndCategoryRevenueDTO> getCategoryRevenue(int storeId, String period, String timeValue);
    List<ProductAndCategoryRevenueDTO> getProductRevenue(int storeId, String period, String timeValue);
}
