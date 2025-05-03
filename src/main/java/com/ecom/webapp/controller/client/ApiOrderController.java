package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.dto.ErrorResponse;
import com.ecom.webapp.model.dto.OrderDto;
import com.ecom.webapp.model.dto.OrderUpdateDto;
import com.ecom.webapp.model.responseDto.OrderResponse;
import com.ecom.webapp.service.OrderService;
import com.ecom.webapp.service.vnpay.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secure/orders")
public class ApiOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private VNPAYService vnpayService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getOrders(Principal principal,
           @RequestParam(value = "status", required = false, defaultValue = "") String status) {
        String username = principal.getName();
        System.out.println(username);
        List<OrderResponse> orders = this.orderService.getOrdersByUsername(username, status);
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
        System.out.println("ORDER-DTO: "+orderDto);

        if (!orderDto.getPaymentMethod().equals("VNPay") && !orderDto.getPaymentMethod().equals("COD")) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Phương thức thanh toán không hợp lệ!"));
        }
        String username = principal.getName();
        orderDto.setUsername(username);
        this.orderService.createOrder(orderDto);

        if ("VNPay".equals(orderDto.getPaymentMethod())) {
            int amount = orderDto.getTotal().intValue();

            String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
            String paymentUrl = vnpayService.createOrder(
                    request,
                    amount,
                    "Thanh toán đơn hàng cho " + orderDto.getUsername(),
                    baseUrl
            );
            return ResponseEntity.ok(Collections.singletonMap("paymentUrl", paymentUrl));
        }

        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }

//    @GetMapping("/vnpay-payment-return")
//    public ResponseEntity<?> handleVnpayReturn(HttpServletRequest request) {
//        Map<String, String> fields = vnpayService.extractVnpParams(request);
//
//        // Kiểm tra checksum hợp lệ
//        boolean isValidChecksum = vnpayService.validateChecksum(fields);
//        if (!isValidChecksum) {
//            return ResponseEntity.badRequest().body("Checksum không hợp lệ");
//        }
//
//        String responseCode = fields.get("vnp_ResponseCode");
//        String orderIdStr = fields.get("vnp_TxnRef"); // Giả sử vnp_TxnRef = orderId
//        Long orderId;
//
//        try {
//            orderId = Long.parseLong(orderIdStr);
//        } catch (NumberFormatException e) {
//            return ResponseEntity.badRequest().body("Mã đơn hàng không hợp lệ");
//        }
//
//        if ("00".equals(responseCode)) {
//            // Giao dịch thành công
//            paymentService.markPaymentAsPaid(orderId);
//            return ResponseEntity.ok("Thanh toán thành công cho đơn hàng #" + orderId);
//        } else {
//            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
//                    .body("Thanh toán thất bại. Mã phản hồi: " + responseCode);
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
