package com.ecom.webapp.repository;

import com.ecom.webapp.model.Order;
import com.ecom.webapp.model.dto.ProductAndCategoryRevenueDTO;
import com.ecom.webapp.model.dto.RevenueDTO;
import com.ecom.webapp.model.dto.StatisticsDTO;
import jakarta.persistence.Tuple;

import java.util.List;
import java.util.Map;

public interface RevenueRepository {
    List<Tuple> getRevenueDataByPeriod(int storeId, String period);

    List<Tuple> getRevenueByProductRaw(int storeId, String period, String timeValue);

    List<Tuple> getRevenueByCategoryRaw(int storeId, String period, String timeValue);

    Tuple getOverallStatisticsRaw(String timeRange, String specificTime);

    List<Tuple> getChartRevenueRaw(String timeRange, String specificTime);

    List<Tuple> getTopProductRevenueRaw(String timeRange, String specificTime, int limit);

    List<Tuple> getTopStoreRevenueRaw(String timeRange, String specificTime, int limit);

    List<Tuple> getStorePerformanceRaw(String timeRange, String specificTime);

}
