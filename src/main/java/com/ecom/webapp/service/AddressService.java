package com.ecom.webapp.service;

import com.ecom.webapp.model.Address;
import com.ecom.webapp.model.dto.AddressDto;
import com.ecom.webapp.model.responseDto.AddressResponse;

import java.util.List;

public interface AddressService {
    List<Address> getAddressesByUserName(String username);
    AddressResponse createAddress(AddressDto address);
    void updateAddress(AddressDto address);
    void deleteAddress(AddressDto address);

}
