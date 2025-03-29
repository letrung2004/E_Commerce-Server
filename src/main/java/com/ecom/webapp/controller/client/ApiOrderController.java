package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.Order;
import com.ecom.webapp.model.dto.ErrorResponse;
import com.ecom.webapp.model.dto.OrderDto;
import com.ecom.webapp.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiOrderController {

    @Autowired
    private OrderService orderService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders(@Valid @RequestBody String username) {
        System.out.println(username);
        return new ResponseEntity<>(this.orderService.getOrdersByUsername(username), HttpStatus.OK);
    }

    @PostMapping("/place-order")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDto orderDto) {
        System.out.println(orderDto);

        if (!orderDto.getPaymentMethod().equals("VNPay") && !orderDto.getPaymentMethod().equals("COD")) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Phương thức thanh toán không hợp lệ!"));
        }

        this.orderService.createOrder(orderDto);
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }


}
