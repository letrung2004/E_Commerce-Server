package com.ecom.webapp.controller.client;


import com.ecom.webapp.model.Cart;
import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.CartDTO;
import com.ecom.webapp.service.CartService;
import com.ecom.webapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/secure/cart")
@CrossOrigin
public class ApiCartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<?> getUserCart(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }
        User user = this.userService.getUserByUsername(principal.getName());
        CartDTO cartDTO = cartService.getCartDetails(user.getId());
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }


    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addUserCart(
            Principal principal,
            @PathVariable(value = "productId") int productId,
            @RequestParam(name = "quantity", required = false, defaultValue = "1") int quantity ) {

        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }

        User user = this.userService.getUserByUsername(principal.getName());
        int userId = user.getId();
        System.out.println("userId: " + userId + ", productId: " + productId + ", quantity: " + quantity);

        CartDTO updatedCart = this.cartService.handelAddProductToCart(userId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }


    @DeleteMapping("/remove/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserCart(
            Principal principal,
            @PathVariable(value = "productId") int productId
    ) {
        if (principal == null || principal.getName() == null) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }
//        problem
        User user = this.userService.getUserByUsername(principal.getName());
        int userId = user.getId();
        this.cartService.handelRemoveProductFromCart(userId, productId);
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<String> updateQuantity(
            @RequestParam(value = "subCartId") int subCartId,
            @RequestParam(value = "productId") int productId,
            @RequestParam(value = "quantityChange") int quantityChange) {
        boolean success = cartService.updateQuantity(subCartId, productId, quantityChange);

        if (success) {
            return ResponseEntity.ok("Cập nhật thành công");
        }
        return ResponseEntity.badRequest().body("Cập nhật thất bại");
    }

}
