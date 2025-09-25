package com.sakibee.service;

import com.sakibee.model.Cart;

import java.util.List;

public interface CartService {
    public Cart saveCart(Integer productId, Integer userId);
    public List<Cart> getCartByUser(Integer userId);

    public Integer getCartProductCount(Integer userId);
    public void updateCartQuantity(Integer cardId, String symbol);
}
