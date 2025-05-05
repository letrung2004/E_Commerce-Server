package com.ecom.webapp.controller.client;

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
    private VNPayService vnPayService;

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
    public synchronized ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderDto orderDto,
            HttpServletRequest request,
            Principal principal) {

        String txnId = String.valueOf(System.currentTimeMillis());
        orderDto.setTransactionId(txnId);
        System.out.println("ORDER-DTO: "+orderDto);

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
