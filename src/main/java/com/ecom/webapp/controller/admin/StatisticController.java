package com.ecom.webapp.controller.admin;

import com.ecom.webapp.model.dto.ProductAndCategoryRevenueDTO;
import com.ecom.webapp.model.dto.RevenueDTO;
import com.ecom.webapp.model.dto.StatisticsDTO;
import com.ecom.webapp.model.dto.StorePerformanceDTO;
import com.ecom.webapp.repository.RevenueRepository;
import com.ecom.webapp.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Controller
public class StatisticController {
    @Autowired
    private RevenueRepository revenueRepository;
    @Autowired
    private RevenueService revenueService;

    @GetMapping("/admin/statistic")
    public String getStatisticAdmin( @RequestParam(name = "timeRange", defaultValue = "month") String timeRange,
                                   @RequestParam(name = "specificTime", required = false) String specificTime,
                                   Model model) {
        int currentYear = Year.now().getValue();
        int currentMonth = LocalDate.now().getMonthValue();
        int currentQuarter = (currentMonth - 1) / 3 + 1;

        if (specificTime == null || specificTime.isEmpty()) {
            switch (timeRange) {
                case "month" -> specificTime = String.valueOf(currentMonth);
                case "quarter" -> specificTime = String.valueOf(currentQuarter);
                case "year" -> specificTime = String.valueOf(currentYear);
            }
        }

        StatisticsDTO overallStats = revenueService.getOverallStatistics(timeRange, specificTime);
        model.addAttribute("overallStats", overallStats);

        List<RevenueDTO> chartData = revenueService.getChartRevenue(timeRange, specificTime);
        List<Object> chartLabels = chartData.stream().map(RevenueDTO::getPeriod).toList();
        List<BigDecimal> chartValues = chartData.stream().map(RevenueDTO::getTotalRevenue).toList();
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartValues", chartValues);

        List<ProductAndCategoryRevenueDTO> topStores = revenueService.getTopStores(timeRange, specificTime,5);
        List<?> storeLabels = topStores.stream().map(ProductAndCategoryRevenueDTO::getName).toList();
        List<BigDecimal> storeValues = topStores.stream().map(ProductAndCategoryRevenueDTO::getTotalRevenue).toList();
        model.addAttribute("storeLabels", storeLabels);
        model.addAttribute("storeValues", storeValues);

        List<ProductAndCategoryRevenueDTO> topProducts = revenueService.getTopProducts(timeRange, specificTime,5);
        List<?> productLabels = topProducts.stream().map(ProductAndCategoryRevenueDTO::getName).toList();
        List<Long> productValues = topProducts.stream().map(ProductAndCategoryRevenueDTO::getQuantitySold).toList();
        model.addAttribute("productLabels", productLabels);
        model.addAttribute("productValues", productValues);

        List<StorePerformanceDTO> storePerformances = revenueService.getStorePerformance(timeRange, specificTime);
        model.addAttribute("storePerformances", storePerformances);

        System.out.println("timeRange check: " + timeRange);
        System.out.println("specificTime check: "+specificTime);
        System.out.println("topStores - storeLabels: " + storeLabels);
        System.out.println("topStores- storeLabels: " + storeValues);
        model.addAttribute("timeRange", timeRange);
        model.addAttribute("specificTime", specificTime);
        return "admin/statistic/statistic";
    }
}
