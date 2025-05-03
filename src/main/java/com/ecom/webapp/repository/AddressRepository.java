package com.ecom.webapp.repository;

import com.ecom.webapp.model.Address;
import com.ecom.webapp.model.User;

import java.util.List;

public interface AddressRepository {
    List<Address> getAddressesByUser(User user, Boolean defaultAddress);
    Address getAddressById(int id);
    Address getDefaultAddressByUser(User user);
    void createAddress(Address address);
    void updateAddress(Address address);
    void deleteAddress(Address address);
}
