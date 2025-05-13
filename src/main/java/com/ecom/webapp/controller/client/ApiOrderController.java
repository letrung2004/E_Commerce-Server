package com.ecom.webapp.controller.client;

//import com.ecom.webapp.config.RedisLockManager;
import com.ecom.webapp.model.dto.ErrorResponse;
import com.ecom.webapp.model.dto.OrderDto;
import com.ecom.webapp.model.dto.OrderUpdateDto;
import com.ecom.webapp.model.responseDto.OrderResponse;
import com.ecom.webapp.service.OrderService;
import com.ecom.webapp.service.impl.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Lớp dùng để lấy instance của Logger

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

@RestController
@RequestMapping("/api/secure/orders")
public class ApiOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private VNPayService vnPayService;
    private static final Logger log = LoggerFactory.getLogger(ApiOrderController.class);
//    @Autowired
//    private RedisLockManager redisLockManager;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getOrders(
            Principal principal,
            @RequestParam(value = "status", required = false, defaultValue = "") String status,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        String username = principal.getName();
        System.out.println(username);
        List<OrderResponse> orders = this.orderService.getOrdersByUsername(username, status, page);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // get an order by id
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable(value = "id") String id) {
        System.out.println(id);
        int orderId = Integer.parseInt(id);
        return new ResponseEntity<>(this.orderService.getOrdersById(orderId), HttpStatus.OK);
    }


    @PostMapping("/place-order")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderDto orderDto,
            HttpServletRequest request,
            Principal principal) {

        String txnId = String.valueOf(System.currentTimeMillis());
        orderDto.setTransactionId(txnId);
        System.out.println("ORDER-DTO: " + orderDto);

        if (!orderDto.getPaymentMethod().equals("VNPay") && !orderDto.getPaymentMethod().equals("COD")) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Phương thức thanh toán không hợp lệ!"));
        }
        String username = principal.getName();
        orderDto.setUsername(username);
        this.orderService.createOrder(orderDto);

        // Nếu là thanh toán VNPay, tạo link và trả về
        if ("VNPay".equalsIgnoreCase(orderDto.getPaymentMethod())) {
            String paymentUrl = vnPayService.createPaymentUrl(request, orderDto);
            return ResponseEntity.ok(Collections.singletonMap("paymentUrl", paymentUrl));
        }


        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }

//    @PostMapping("/place-order")
//    public ResponseEntity<?> createOrder(
//            @Valid @RequestBody OrderDto orderDto,
//            HttpServletRequest request,
//            Principal principal) {
//
//        // Generate a more unique transaction ID
//        String txnId = UUID.randomUUID().toString();
//        orderDto.setTransactionId(txnId);
//
//        // Log with proper logging framework instead of System.out
//        log.info("ORDER-DTO: {}", orderDto);
//

    /// /        if (!orderDto.getPaymentMethod().equals("VNPay") && !orderDto.getPaymentMethod().equals("COD")) {
    /// /            return ResponseEntity.badRequest().body(new ErrorResponse("Phương thức thanh toán không hợp lệ!"));
    /// /        }
//        List<String> validMethods = List.of("VNPay", "COD");
//        if (!validMethods.contains(orderDto.getPaymentMethod())) {
//            return ResponseEntity.badRequest().body(new ErrorResponse("Phương thức thanh toán không hợp lệ!"));
//        }
//
//
//        String username = principal.getName();
//        orderDto.setUsername(username);
//
//        String userLockKey = "order-lock-" + username;
//        boolean lockAcquired = false;
//
//        try {
//            // Add distributed lock for this user's orders
//            // Use tryLock with timeout to avoid indefinite blocking
//            lockAcquired = redisLockManager.tryAcquire(userLockKey, 5, 30, TimeUnit.SECONDS); // Giả định acquireLock trả về boolean
//
//            if (!lockAcquired) {
//                // Could not acquire lock within the timeout, likely another request from the same user is in progress
//                log.warn("Could not acquire lock for user: {}", username);
//                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
//                        .body(new ErrorResponse("Bạn đang có đơn hàng khác đang được xử lý. Vui lòng thử lại sau giây lát."));
//            }
//
//            // Process the order after acquiring the lock
//            // This method should handle internal concurrency (transactions, db locks for shared resources)
//            this.orderService.createOrder(orderDto);
//
//            // Handle VNPAY payment
//            if ("VNPay".equalsIgnoreCase(orderDto.getPaymentMethod())) {
//                String paymentUrl = vnPayService.createPaymentUrl(request, orderDto);
//                return ResponseEntity.ok(Collections.singletonMap("paymentUrl", paymentUrl));
//            }
//
//            return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
//
//        } catch (Exception e) {
//            log.error("Error creating order for user: {}", username, e);
//            // Handle specific exceptions if needed, e.g., insufficient stock
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Lỗi khi xử lý đơn hàng: " + e.getMessage()));
//        } finally {
//            if (lockAcquired) {
//                redisLockManager.releaseLock(userLockKey);
//            }
//        }
//    }
    @PatchMapping("/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOrder(@Valid @RequestBody OrderUpdateDto orderUpdateDto) {
        System.out.println(orderUpdateDto);
        this.orderService.updateOrder(orderUpdateDto);
    }


    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable(value = "id") int id) {
        this.orderService.deleteOrder(id);
    }

}
