package com.ecom.webapp.controller.client;


import com.ecom.webapp.model.Cart;
import com.ecom.webapp.model.dto.CartDTO;
import com.ecom.webapp.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class ApiCartController {
    @Autowired
    private CartService cartService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserCart(@PathVariable(value = "userId") int userId) {
        CartDTO cartDTO = cartService.getCartDetails(userId);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }


    @PostMapping("/{userId}/add/{productId}")
    public ResponseEntity<CartDTO> addUserCart(
            @PathVariable(value = "userId") int userId,
            @PathVariable(value = "productId") int productId) {
        System.out.println("userId: " + userId + ", productId: " + productId);
        CartDTO updatedCart = this.cartService.handelAddProductToCart(userId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserCart(
            @PathVariable(value = "userId") int userId,
            @PathVariable(value = "productId") int productId
    ) {
       this.cartService.handelRemoveProductFromCart(userId, productId);
    }

    @PatchMapping("/update-quantity")
    public ResponseEntity<String> updateQuantity(
            @RequestParam int subCartId,
            @RequestParam int productId,
            @RequestParam int quantityChange) {
        boolean success = cartService.updateQuantity(subCartId, productId, quantityChange);

        if (success) {
            return ResponseEntity.ok("Cập nhật thành công");
        }
        return ResponseEntity.badRequest().body("Cập nhật thất bại");
    }

}
