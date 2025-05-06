package com.ecom.webapp.repository;

import com.ecom.webapp.model.Store;

import java.util.List;
import java.util.Map;

public interface StoreRepository {
    List<Store> getStores(Map<String, String> params);
    List<Object[]> getStoresUnprocessed();
    Store getStoreByUsername(String username);
    Store getStoreById(int id);
    void createStore(Store store);
    void updateStore(Store store);
    void deleteStore(Store store);
}
