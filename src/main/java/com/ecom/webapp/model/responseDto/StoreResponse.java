package com.ecom.webapp.model.responseDto;

import com.ecom.webapp.model.Store;
import lombok.Data;

@Data
public class StoreResponse {
    private int id;
    private String name;

    public StoreResponse(Store store) {
        this.id = store.getId();
        this.name = store.getName();
    }
}
