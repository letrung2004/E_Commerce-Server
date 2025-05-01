package com.ecom.webapp.service;

import com.ecom.webapp.model.Store;
import com.ecom.webapp.model.dto.StoreDto;
import com.ecom.webapp.model.responseDto.StoreResponse;

import java.util.List;

public interface StoreService {
    void createStore(StoreDto storeDto);
    void updateStore(int store);
    Store getStoreByUsername(String username);

    List<Store> getStores();
    List<Object[]> getStoresUnprocessed();
    void deleteStore(int storeId);
    StoreResponse getStoreById(int storeId);
    int getStoreIdByUsername(String username);
}
