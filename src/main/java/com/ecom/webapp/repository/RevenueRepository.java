package com.ecom.webapp.repository;

import com.ecom.webapp.model.Order;
import com.ecom.webapp.model.dto.ProductAndCategoryRevenueDTO;
import com.ecom.webapp.model.dto.RevenueDTO;

import java.util.List;
import java.util.Map;

public interface RevenueRepository {
    List<RevenueDTO> getRevenueByPeriod(int storeId, String period);
    List<ProductAndCategoryRevenueDTO> getRevenueByProduct(int storeId, String period, String timeValue);
    List<ProductAndCategoryRevenueDTO> getRevenueByCategory(int storeId, String period, String timeValue);
}
