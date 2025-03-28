package com.ecom.webapp.repository;

import com.ecom.webapp.model.Store;

import java.util.List;

public interface StoreRepository {
    List<Store> getStores();
    List<Object[]> getStoresUnprocessed();
    Store getStoreByUsername(String username);
    Store getStoreById(int id);
    void createStore(Store store);
    void updateStore(Store store);
    void deleteStore(Store store);
}
