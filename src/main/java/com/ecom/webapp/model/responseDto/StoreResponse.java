package com.ecom.webapp.model.responseDto;

import lombok.Data;

@Data
public class StoreResponse {
    private int id;
    private String name;
    private String logo;
    private String description;
    private String phoneNumber;
    private String ownerName;
    private String addressLine;
}
