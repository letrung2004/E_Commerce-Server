package com.ecom.webapp.service;

import com.ecom.webapp.model.Address;
import com.ecom.webapp.model.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<Address> getAddressesByUserName(String username);
    void createAddress(AddressDto address);
    void updateAddress(AddressDto address);
    void deleteAddress(AddressDto address);

}
