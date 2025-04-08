package com.ecom.webapp.repository;

import com.ecom.webapp.model.SubCart;

import java.util.List;

public interface SubCartRepository {
    List<SubCart> getSubCartsByCartId(int cartId);
    SubCart getByCartIdAndStoreId(int cartId, int storeId);
    void save(SubCart subCart);
    SubCart getById(int subCartId);
    void deleteSubCart(SubCart subCart);
}
