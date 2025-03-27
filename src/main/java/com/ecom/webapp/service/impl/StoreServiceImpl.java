package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.Store;
import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.StoreDto;
import com.ecom.webapp.repository.StoreRepository;
import com.ecom.webapp.repository.UserRepository;
import com.ecom.webapp.service.StoreService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void createStore(StoreDto storeDto) {
        User owner = this.userRepository.getUserByUsername(storeDto.getUsername());
        if (owner == null) {
            throw new EntityNotFoundException("User not found with name " + storeDto.getUsername());
        }
        Store store = new Store();
        store.setName(storeDto.getName());
        store.setDescription(storeDto.getDescription());
        store.setLogo(storeDto.getLogo());
        store.setOwner(owner);

        this.storeRepository.createStore(store);
    }

    @Override
    public Store getStoreByUsername(String username) {
        Store store = this.storeRepository.getStoreByUsername(username);
        if (store == null) {
            throw new EntityNotFoundException("Store not found with username: " + username);
        }
        return store;
    }

    @Override
    public List<Store> getStores() {
        return this.storeRepository.getStores();
    }

    @Override
    public List<Object[]> getStoresUnprocessed() {
        return this.storeRepository.getStoresUnprocessed();
    }

    @Override
    public void updateStore(int store) {

    }

    @Override
    public void deleteStore(int storeId) {
        Store store = this.storeRepository.getStoreById(storeId);
        if (store == null) {
            throw new EntityNotFoundException("Store not found with id " + storeId);
        }
        this.storeRepository.deleteStore(store);
    }
}
