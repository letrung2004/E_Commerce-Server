package com.ecom.webapp.model.responseDto;


import com.ecom.webapp.model.Address;
import lombok.Data;

@Data
public class AddressResponse {
    private int id;
    private String username;
    private String receiver;
    private String phoneNumber;
    private String address;
    private Boolean defaultAddress;

    public AddressResponse(Address address) {
        this.id = address.getId();
        this.username = address.getUser().getUsername();
        this.receiver = address.getReceiver();
        this.phoneNumber = address.getPhoneNumber();
        this.address = address.getAddress();
        this.defaultAddress = address.getDefaultAddress();
    }
}
