package com.ecom.webapp.service;

import com.ecom.webapp.model.dto.AddressDto;
import com.ecom.webapp.model.responseDto.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAddressesByUserName(String username, Boolean defaultAddress);
    AddressResponse createAddress(AddressDto address);
    void updateAddress(AddressDto address);
    void setDefaultAddress(int id, String username);
    AddressResponse deleteAddress(int id);

}
