package com.ecom.webapp.service;

import com.ecom.webapp.model.dto.AddressDto;
import com.ecom.webapp.model.responseDto.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAddressesByUserName(String username);
    AddressResponse createAddress(AddressDto address);
    void updateAddress(AddressDto address);
    AddressResponse deleteAddress(int id);

}
