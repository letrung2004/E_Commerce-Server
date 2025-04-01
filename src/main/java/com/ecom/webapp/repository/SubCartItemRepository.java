package com.ecom.webapp.repository;

import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.SubCartItem;

import java.util.List;

public interface SubCartItemRepository {
    SubCartItem getSubCartItemById(int subCartItemId);
    List<SubCartItem> getSubCartItemsBySubCartId(List<Integer> subCartIds);
    void save(SubCartItem subCartItem);
    SubCartItem getBySubCartIdAndProductId(int subCartId, int productId);
    void delete(SubCartItem subCartItem);
    int countBySubCartId(int subCartId);
}
