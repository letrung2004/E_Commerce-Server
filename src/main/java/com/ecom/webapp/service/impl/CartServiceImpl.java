package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.*;
import com.ecom.webapp.model.dto.CartDTO;
import com.ecom.webapp.repository.*;
import com.ecom.webapp.service.CartService;
import com.ecom.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private UserService userService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private SubCartRepository subCartRepository;
    @Autowired
    private SubCartItemRepository subCartItemRepository;
    @Autowired
    private ProductRepository productRepository;


    @Override
    @Transactional
    public CartDTO getCartDetails(int userId) {
        Cart cart = cartRepository.getCartByUserId(userId);

        if (cart == null) {
            return null;
        }

        List<SubCart> subCarts = subCartRepository.getSubCartsByCartId(cart.getId());
        List<Integer> subCartIds = subCarts.stream().map(SubCart::getId).toList();
        List<SubCartItem> subCartItems = subCartItemRepository.getSubCartItemsBySubCartId(subCartIds);

        return new CartDTO(cart,subCarts,subCartItems);
    }


    @Override
    @Transactional
    public CartDTO handelAddProductToCart(int userId, int productId, int quantity) {
        int actualQuantity = (quantity <= 0) ? 1 : quantity;
        User user = this.userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("Khong tim thay nguoi dung");
        }

        Cart cart = cartRepository.getCartByUserId(userId);
        if (cart == null) {
             cart = this.cartRepository.createNewCart(userId);
        }

        Product product = this.productRepository.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        int storeId = product.getStore().getId();

        SubCart subCart = this.subCartRepository.getByCartIdAndStoreId(cart.getId(), storeId);
        if (subCart == null) {
            subCart = new SubCart();
            subCart.setCart(cart);
            subCart.setStore(product.getStore());
            subCartRepository.save(subCart);
        }
        boolean isNewItem = false;
        SubCartItem subCartItem = this.subCartItemRepository.getBySubCartIdAndProductId(subCart.getId(), productId);
        if (subCartItem == null) {
            isNewItem = true;
            subCartItem = new SubCartItem();
            subCartItem.setSubCart(subCart);
            subCartItem.setProduct(product);
            subCartItem.setQuantity(actualQuantity);
            subCartItem.setUnitPrice(product.getPrice());
            subCartItemRepository.save(subCartItem);
        } else {
            subCartItem.setQuantity(subCartItem.getQuantity() + actualQuantity);
            subCartItemRepository.save(subCartItem);
        }


        System.out.println("SERVICE - subcart item: " + subCartItem.getId() );
        if (isNewItem) {
            cart.setItemsNumber(cart.getItemsNumber() + 1);
        }
        this.cartRepository.updateCart(cart);

        return new CartDTO(cart, List.of(subCart), List.of(subCartItem));
    }

    @Override
    public void handelRemoveProductFromCart(int userId, int productId) {
        User user = this.userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Cart cart = cartRepository.getCartByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }
        Product product = this.productRepository.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        int storeId = product.getStore().getId();
        SubCart subCart = this.subCartRepository.getByCartIdAndStoreId(cart.getId(), storeId);
        SubCartItem subCartItem = this.subCartItemRepository.getBySubCartIdAndProductId(subCart.getId(), productId);
        if (subCartItem == null) throw new RuntimeException("Product not in cart");

        //xoa Cart, SubCart, SubCartItem
        cart.setItemsNumber(cart.getItemsNumber() - 1);
        this.subCartItemRepository.deleteSubCartItem(subCartItem);

        int subCartItemCount = this.subCartItemRepository.countBySubCartId(subCart.getId());
        if (subCartItemCount == 0) {
            this.subCartRepository.deleteSubCart(subCart);
        }


        if (cart.getItemsNumber() <= 0) {
            this.cartRepository.deleteCart(cart);
        } else {
            this.cartRepository.updateCart(cart);
        }
    }

    @Override
    @Transactional
    public boolean updateQuantity(int subCartId, int productId, int quantityChange) {
        Optional<SubCart> optionalSubCart = Optional.ofNullable(subCartRepository.getById(subCartId));
        if (optionalSubCart.isPresent()) {
            SubCart subCart = optionalSubCart.get();

            for (SubCartItem item : subCart.getSubCartItems()) {
                if (item.getProduct().getId().equals(productId)) {
                    int newQuantity = item.getQuantity() + quantityChange;

                    if (newQuantity > 0) {
                        item.setQuantity(newQuantity);
                    } else {
                        subCart.getSubCartItems().remove(item);
                    }

                    subCartRepository.save(subCart);
                    return true;
                }
            }
        }
        return false;
    }

//    @Override
//    @Transactional
//    public CartDTO checkoutCart(int userId, List<Integer> productIds) {
//        if (productIds == null || productIds.isEmpty()) {
//          return null;
//        }
//
//        Cart cart = cartRepository.getCartByUserId(userId);
//        if (cart == null) return null;
//
//        int removedCount = 0;
//
//        for (Integer productId : productIds) {
//            Product product = productRepository.getProductById(productId);
//            if (product == null) continue;
//
//            int storeId = product.getStore().getId();
//            SubCart subCart = subCartRepository.getByCartIdAndStoreId(cart.getId(), storeId);
//            if (subCart == null) continue;
//
//            SubCartItem item = subCartItemRepository.getBySubCartIdAndProductId(subCart.getId(), productId);
//            if (item != null) {
//                subCartItemRepository.deleteSubCartItem(item);
//                removedCount++;
//            }
//
//            int remaining = subCartItemRepository.countBySubCartId(subCart.getId());
//            if (remaining == 0) {
//                subCartRepository.deleteSubCart(subCart);
//            }
//        }
//        cart.setItemsNumber(cart.getItemsNumber() - removedCount);
//
//        if (cart.getItemsNumber() <= 0) {
//            cartRepository.deleteCart(cart);
//            return null;
//        } else {
//            cartRepository.updateCart(cart);
//            return this.getCartDetails(userId);
//        }
//    }


}
