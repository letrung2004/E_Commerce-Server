package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.Address;
import com.ecom.webapp.model.dto.AddressDto;
import com.ecom.webapp.model.responseDto.AddressResponse;
import com.ecom.webapp.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/secure/address")
public class ApiAddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/create")
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressDto addressDto) {
        System.out.println(addressDto);
        return new ResponseEntity<>(this.addressService.createAddress(addressDto), HttpStatus.CREATED);
    }
}
