package com.sakibee.impl;

import com.sakibee.model.Cart;
import com.sakibee.model.Product;
import com.sakibee.model.User;
import com.sakibee.repository.CartRepository;
import com.sakibee.repository.ProductRepository;
import com.sakibee.repository.UserRepository;
import com.sakibee.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {


    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;


    @Override
    public void updateCartQuantity(Integer cardId, String symbol) {
        Cart cart = cartRepository.findById(cardId).get();
        Integer quantity = cart.getQuantity();

        if(symbol.equals("dec")) {
            quantity = quantity - 1;
            if(quantity <= 0) cartRepository.deleteById(cardId);
            else {
                cart.setQuantity(quantity);
                cartRepository.save(cart);
            }
        }
        else {
            quantity = quantity + 1;
            cart.setQuantity(quantity);
            cartRepository.save(cart);
        }


    }
    @Override
    public Integer getCartProductCount(Integer userId) {
        Integer productCount = cartRepository.countByUserId(userId);
        return productCount;
    }
    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        User user = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();

        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

        Cart cart = null;

        if(ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cart.setTotalPrice(1*product.getDiscountPrice());
        } else {
            cart = cartStatus;
            cart.setQuantity(cartStatus.getQuantity()+1);
            cart.setTotalPrice(cartStatus.getQuantity() * product.getDiscountPrice());
        }
        Cart saveCart = cartRepository.save(cart);

        return saveCart;
    }

    @Override
    public List<Cart> getCartByUser(Integer userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);

        Double totalOrderPrice = 0.0;
        List<Cart> updatedCarts = new ArrayList<>();
        for (Cart c : carts) {
            Double totalPrice = c.getProduct().getDiscountPrice() * c.getQuantity();
            double roundTotalPrice = (Math.round(totalPrice * 100)) / 100; //for 2 decimal
            c.setTotalPrice(roundTotalPrice);
            totalOrderPrice += totalPrice;
            double roundTotalOrderPrice = (Math.round(totalOrderPrice * 100)) / 100;
            c.setTotalOrderPrice(roundTotalOrderPrice);
            updatedCarts.add(c);
        }
        return updatedCarts;
    }


}
