package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.Store;
import com.ecom.webapp.model.dto.StoreDto;
import com.ecom.webapp.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiHomeController {

    @Autowired
    private StoreService storeService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/store-activation")
    public ResponseEntity<StoreDto> requestActivationStore(@Valid @RequestBody StoreDto storeDto) {
        System.out.println(storeDto);
        this.storeService.createStore(storeDto);
        return new ResponseEntity<>(storeDto, HttpStatus.CREATED);
    }
}
