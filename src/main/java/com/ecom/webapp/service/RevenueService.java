package com.ecom.webapp.service;

import com.ecom.webapp.model.dto.ProductAndCategoryRevenueDTO;
import com.ecom.webapp.model.dto.RevenueDTO;
import com.ecom.webapp.model.dto.StatisticsDTO;
import com.ecom.webapp.model.dto.StorePerformanceDTO;

import java.util.List;

public interface RevenueService {
    List<RevenueDTO> getRevenueByPeriod(int storeId, String period);
    List<ProductAndCategoryRevenueDTO> getCategoryRevenue(int storeId, String period, String timeValue);
    List<ProductAndCategoryRevenueDTO> getProductRevenue(int storeId, String period, String timeValue);
    //báo cáo cho người quản trị
    StatisticsDTO getOverallStatistics(String timeRange, String specificTime);
    List<RevenueDTO> getChartRevenue(String timeRange, String specificTime);

    List<ProductAndCategoryRevenueDTO> getTopProducts(String timeRange, String specificTime, int limit);
    List<StorePerformanceDTO> getStorePerformance(String timeRange, String specificTime);
    List<ProductAndCategoryRevenueDTO> getTopStores(String timeRange, String specificTime, int limit);
}
