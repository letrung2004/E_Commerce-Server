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

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/secure/address")
public class ApiAddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/create")
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressDto addressDto, Principal principal) {
        String username = principal.getName();
        addressDto.setUsername(username);
        System.out.println(addressDto);
        return new ResponseEntity<>(this.addressService.createAddress(addressDto), HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<List<AddressResponse>> getAllAddresses(
            Principal principal,
            @RequestParam(value = "defaultAddress", required = false) Boolean defaultAddress) {
        String username = principal.getName();
        System.out.println("USERNAME: " + username);
        return new ResponseEntity<>(this.addressService.getAddressesByUserName(username, defaultAddress), HttpStatus.OK);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<AddressResponse> removeAddress(@PathVariable("id") int id) {
        return new ResponseEntity<>(this.addressService.deleteAddress(id), HttpStatus.OK);
    }

    @PatchMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateAddress(@Valid @RequestBody AddressDto addressDto) {
        this.addressService.updateAddress(addressDto);
    }

    @PatchMapping("/set-default/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void setDefaultAddress(@PathVariable("id") int id, Principal principal) {
        String username = principal.getName();
        this.addressService.setDefaultAddress(id,username);
    }
}
