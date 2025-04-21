package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.Address;
import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.AddressDto;
import com.ecom.webapp.model.responseDto.AddressResponse;
import com.ecom.webapp.repository.AddressRepository;
import com.ecom.webapp.repository.UserRepository;
import com.ecom.webapp.service.AddressService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Address> getAddressesByUserName(String username) {
        User user = this.userRepository.getUserByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with name: " + username);
        }
        return this.addressRepository.getAddressesByUser(user);
    }

    @Override
    public AddressResponse createAddress(AddressDto addressDto) {
        User user = this.userRepository.getUserByUsername(addressDto.getUsername());
        if (user == null) {
            throw new EntityNotFoundException("User not found with name: " + addressDto.getUsername());
        }
        Address address = new Address();
        address.setAddress(addressDto.getAddress());
        address.setReceiver(addressDto.getReceiver());
        address.setPhoneNumber(addressDto.getPhoneNumber());
        address.setUser(user);
        this.addressRepository.createAddress(address);
        return new AddressResponse(address);
    }

    @Override
    public void updateAddress(AddressDto addressDto) {
        Address address = this.addressRepository.getAddressById(addressDto.getId());
        if (address == null) {
            throw new EntityNotFoundException("Address not found with id: " + addressDto.getId());
        }
        address.setAddress(addressDto.getAddress());
        address.setReceiver(addressDto.getReceiver());
        address.setPhoneNumber(addressDto.getPhoneNumber());
        this.addressRepository.updateAddress(address);
    }

    @Override
    public void deleteAddress(AddressDto addressDto) {
        Address address = this.addressRepository.getAddressById(addressDto.getId());
        if (address == null) {
            throw new EntityNotFoundException("Address not found with id: " + addressDto.getId());
        }
        this.addressRepository.deleteAddress(address);
    }
}
