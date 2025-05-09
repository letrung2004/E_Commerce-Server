package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.dto.RevenueDTO;
import com.ecom.webapp.service.RevenueService;
import com.ecom.webapp.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/secure")
public class ApiRevenue {
    @Autowired
    private RevenueService revenueService;
    @Autowired
    private StoreService storeService;

    @GetMapping("revenue")
    public ResponseEntity<?> getRevenueByPeriod(
            Principal principal,
            @RequestParam(value = "period") String period
    ) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }

        String username = principal.getName();
        int storeId = this.storeService.getStoreIdByUsername(username);
        System.out.println(storeId);
        List<RevenueDTO> data = revenueService.getRevenueByPeriod(storeId, period);

        return ResponseEntity.ok(data);
    }

    @GetMapping("product-revenue")
    public ResponseEntity<?> getProductRevenueByPeriod( Principal principal,
                                                        @RequestParam(defaultValue = "month", value = "period") String period,
                                                        @RequestParam(required = false, value = "timeValue") String timeValue){
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }

        String username = principal.getName();
        int storeId = this.storeService.getStoreIdByUsername(username);
        return new ResponseEntity<>(
                revenueService.getProductRevenue(storeId, period, timeValue),
                HttpStatus.OK
        );
    }

    @GetMapping("category-revenue")
    public ResponseEntity<?> getCategoryRevenueByPeriod(Principal principal,
                                                        @RequestParam(defaultValue = "month", value = "period") String period,
                                                        @RequestParam(required = false, value = "timeValue") String timeValue){
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }

        String username = principal.getName();
        int storeId = this.storeService.getStoreIdByUsername(username);
        return new ResponseEntity<>(
                revenueService.getCategoryRevenue(storeId, period, timeValue),
                HttpStatus.OK
        );
    }

}
