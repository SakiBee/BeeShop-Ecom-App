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

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    
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
        return List.of();
    }
}
