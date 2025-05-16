package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.dto.ProductAndCategoryRevenueDTO;
import com.ecom.webapp.model.dto.RevenueDTO;
import com.ecom.webapp.model.dto.StatisticsDTO;
import com.ecom.webapp.model.dto.StorePerformanceDTO;
import com.ecom.webapp.repository.RevenueRepository;
import com.ecom.webapp.repository.StoreRepository;
import com.ecom.webapp.service.RevenueService;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class RevenueServiceImpl implements RevenueService {
    @Autowired
    private RevenueRepository revenueRepository;
    @Autowired
    private StoreRepository storeRepository;

    @Override
    public List<RevenueDTO> getRevenueByPeriod(int storeId, String period) {
        List<Tuple> result = revenueRepository.getRevenueDataByPeriod(storeId, period);
        Map<String, RevenueDTO> resultMap = new HashMap<>();

        for (Tuple r : result) {
            Object periodValue = r.get("period");
            String periodStr = periodValue != null ? periodValue.toString() : "UNKNOWN";

            RevenueDTO dto = new RevenueDTO();
            dto.setPeriod(periodStr);
            dto.setTotalRevenue(r.get("totalRevenue", BigDecimal.class));
            dto.setOrderCount(r.get("orderCount", Long.class));
            dto.setAveragePrice(r.get("averagePrice", BigDecimal.class));

            resultMap.put(periodStr, dto);
        }

        List<String> allPeriods = generateAllPeriods(period);

        List<RevenueDTO> finalResult = new ArrayList<>();
        for (String p : allPeriods) {
            finalResult.add(resultMap.getOrDefault(p, new RevenueDTO(p, BigDecimal.ZERO, 0L, BigDecimal.ZERO)));
        }

        return finalResult;
    }

    @Override
    public List<ProductAndCategoryRevenueDTO> getCategoryRevenue(int storeId, String period, String timeValue) {
        List<Tuple> results = revenueRepository.getRevenueByCategoryRaw(storeId, period, timeValue);
        List<ProductAndCategoryRevenueDTO> dtoList = new ArrayList<>();

        for (Tuple r : results) {
            ProductAndCategoryRevenueDTO dto = new ProductAndCategoryRevenueDTO();
            dto.setId(r.get("categoryId", Integer.class));
            dto.setName(r.get("categoryName", String.class));
            dto.setPeriod(timeValue);

            BigDecimal revenue = r.get("totalRevenue", BigDecimal.class);
            dto.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

            Long oCount = r.get("orderCount", Long.class);
            dto.setOrderCount(oCount != null ? oCount : 0L);

            Long pCount = r.get("productCount", Long.class);
            dto.setQuantitySold(pCount != null ? pCount : 0L);

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public List<ProductAndCategoryRevenueDTO> getProductRevenue(int storeId, String period, String timeValue) {
        List<Tuple> results = revenueRepository.getRevenueByProductRaw(storeId, period, timeValue);
        List<ProductAndCategoryRevenueDTO> dtoList = new ArrayList<>();

        for (Tuple r : results) {
            ProductAndCategoryRevenueDTO dto = new ProductAndCategoryRevenueDTO();
            dto.setId(r.get("productId", Integer.class));
            dto.setName(r.get("productName", String.class));
            dto.setPeriod(timeValue);

            BigDecimal revenue = r.get("totalRevenue", BigDecimal.class);
            dto.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

            Long quantity = r.get("quantitySold", Long.class);
            dto.setQuantitySold(quantity != null ? quantity : 0L);

            Long oCount = r.get("orderCount", Long.class);
            dto.setOrderCount(oCount != null ? oCount : 0L);

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public StatisticsDTO getOverallStatistics(String timeRange, String specificTime) {
        Tuple result = revenueRepository.getOverallStatisticsRaw(timeRange, specificTime);

        if (result == null) {
            return new StatisticsDTO(0L, 0.0, 0L);
        }

        return new StatisticsDTO(
                Optional.ofNullable(result.get("totalOrders", Long.class)).orElse(0L),
                Optional.ofNullable(result.get("totalRevenue", BigDecimal.class)).orElse(BigDecimal.ZERO).doubleValue(),
                Optional.ofNullable(result.get("totalProductsSold", Long.class)).orElse(0L)
        );

    }

    @Override
    public List<RevenueDTO> getChartRevenue(String timeRange, String specificTime) {
        List<Tuple> results = revenueRepository.getChartRevenueRaw(timeRange, specificTime);

        Map<String, RevenueDTO> dataMap = new HashMap<>();
        for (Tuple r : results) {
            String key = r.get("period").toString();
            BigDecimal total = r.get("totalRevenue", BigDecimal.class);
            dataMap.put(key, new RevenueDTO(key, total, null, null));
        }


        List<String> allLabels = generateChartLabels(timeRange, specificTime);

        List<RevenueDTO> finalList = new ArrayList<>();
        for (String label : allLabels) {
            finalList.add(dataMap.getOrDefault(label, new RevenueDTO(label, BigDecimal.ZERO, null, null)));
        }

        return finalList;
    }

    @Override
    public List<ProductAndCategoryRevenueDTO> getTopProducts(String timeRange, String specificTime, int limit) {
        List<Tuple> tuples = revenueRepository.getTopProductRevenueRaw(timeRange, specificTime, limit);
        return tuples.stream().map(t -> {
            ProductAndCategoryRevenueDTO dto = new ProductAndCategoryRevenueDTO();
            dto.setName(t.get("productName", String.class));
            dto.setQuantitySold(Optional.ofNullable(t.get("totalProductsSold", Long.class)).orElse(0L));
            return dto;
        }).collect(Collectors.toList());
    }

    private String getPreviousTime(String timeRange, String current) {
        switch (timeRange) {
            case "month" -> {
                int month = Integer.parseInt(current);
                int prevMonth = (month == 1) ? 12 : month - 1;
                return String.valueOf(prevMonth);
            }
            case "quarter" -> {
                int quarter = Integer.parseInt(current);
                int prevQuarter = (quarter == 1) ? 4 : quarter - 1;
                return String.valueOf(prevQuarter);
            }
            case "year" -> {
                int year = Integer.parseInt(current);
                return String.valueOf(year - 1);
            }
            default -> throw new IllegalArgumentException("Invalid timeRange");
        }
    }


    @Override
    public List<StorePerformanceDTO> getStorePerformance(String timeRange, String specificTime) {
        List<Tuple> currentTuples = revenueRepository.getStorePerformanceRaw(timeRange, specificTime);

        String previousTime = getPreviousTime(timeRange, specificTime);
        List<Tuple> previousTuples = revenueRepository.getStorePerformanceRaw(timeRange, previousTime);

        Map<Integer, BigDecimal> previousRevenueMap = previousTuples.stream()
                .collect(Collectors.toMap(
                        t -> t.get("storeId", Integer.class),
                        t -> t.get("totalRevenue", BigDecimal.class)
                ));

        return currentTuples.stream().map(t -> {
            Integer storeId = t.get("storeId", Integer.class);
            BigDecimal currentRevenue = Optional.ofNullable(t.get("totalRevenue", BigDecimal.class)).orElse(BigDecimal.ZERO);
            BigDecimal previousRevenue = previousRevenueMap.getOrDefault(storeId, BigDecimal.ZERO);

            Double growth = 0.0;
            if (previousRevenue.compareTo(BigDecimal.ZERO) > 0) {
                growth = currentRevenue.subtract(previousRevenue)
                        .divide(previousRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
            }
            StorePerformanceDTO dto = new StorePerformanceDTO();
            dto.setStoreId(Long.valueOf(storeId));
            dto.setStoreName(t.get("storeName", String.class));
            dto.setProductCount(t.get("productCount", Long.class));
            dto.setOrderCount(t.get("orderCount", Long.class));
            dto.setProductsSold(t.get("productsSold", Long.class));
            dto.setTotalRevenue(currentRevenue);
            dto.setGrowthPercentage(growth);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProductAndCategoryRevenueDTO> getTopStores(String timeRange, String specificTime, int limit) {
        List<Tuple> tuples = revenueRepository.getTopStoreRevenueRaw(timeRange, specificTime, limit);
        return tuples.stream().map(t -> {
            ProductAndCategoryRevenueDTO dto = new ProductAndCategoryRevenueDTO();
            dto.setName(t.get("storeName", String.class));
            dto.setTotalRevenue(Optional.ofNullable(t.get("totalRevenue", BigDecimal.class)).orElse(BigDecimal.ZERO));
            return dto;
        }).collect(Collectors.toList());
    }


    private List<String> generateAllPeriods(String period) {
        Year currentYear = Year.now();
        int year = currentYear.getValue();

        switch (period) {
            case "month":
                return IntStream.rangeClosed(1, LocalDate.now().getMonthValue())
                        .mapToObj(m -> String.format("%d-%02d", year, m))
                        .collect(Collectors.toList());

            case "quarter":
                int currentQuarter = (LocalDate.now().getMonthValue() - 1) / 3 + 1;
                return IntStream.rangeClosed(1, currentQuarter)
                        .mapToObj(qtr -> String.format("%d-Q%d", year, qtr))
                        .collect(Collectors.toList());

            case "year":
                return IntStream.rangeClosed(year - 2, year)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.toList());

            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }
    }

    // Helper method to generate chart labels
    private List<String> generateChartLabels(String timeRange, String specificTime) {
        List<String> allLabels = new ArrayList<>();
        int year = Year.now().getValue();

        if (timeRange.equals("year") || timeRange.equals("quarter")) {
            year = Integer.parseInt(specificTime);
        }

        switch (timeRange) {
            case "month":
                for (int i = 1; i <= 31; i++) {
                    allLabels.add(String.format("%d", i));
                }
                break;

            case "quarter":
                int quarter = Integer.parseInt(specificTime);
                int startMonth = (quarter - 1) * 3 + 1;
                int endMonth = startMonth + 2;

                for (int i = startMonth; i <= endMonth; i++) {
                    allLabels.add(String.valueOf(i));
                }
                break;

            case "year":
                for (int i = 1; i <= 12; i++) {
                    allLabels.add(String.format("%d", i));
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid timeRange: " + timeRange);
        }

        return allLabels;
    }
}
