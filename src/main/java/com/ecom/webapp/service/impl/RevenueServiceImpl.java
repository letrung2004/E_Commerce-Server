package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.dto.ProductAndCategoryRevenueDTO;
import com.ecom.webapp.model.dto.RevenueDTO;
import com.ecom.webapp.repository.RevenueRepository;
import com.ecom.webapp.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RevenueServiceImpl implements RevenueService {
    @Autowired
    private RevenueRepository revenueRepository;
    @Override
    public List<RevenueDTO> getRevenueByPeriod(int storeId, String period) {
        return this.revenueRepository.getRevenueByPeriod(storeId, period);
    }

    @Override
    public List<ProductAndCategoryRevenueDTO> getCategoryRevenue(int storeId, String period, String timeValue) {
        return this.revenueRepository.getRevenueByCategory(storeId, period, timeValue);
    }

    @Override
    public List<ProductAndCategoryRevenueDTO> getProductRevenue(int storeId, String period, String timeValue) {
        return this.revenueRepository.getRevenueByProduct(storeId, period, timeValue);
    }
}
