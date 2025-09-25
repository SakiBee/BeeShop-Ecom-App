package com.sakibee.impl;

import com.sakibee.model.Cart;
import com.sakibee.model.OrderAddress;
import com.sakibee.model.OrderRequest;
import com.sakibee.model.ProductOrder;
import com.sakibee.repository.CartRepository;
import com.sakibee.repository.ProductOrderRepository;
import com.sakibee.service.ProductOrderService;
import com.sakibee.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {


    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) {
        List<Cart> carts = cartRepository.findByUserId(userId);

        for (Cart cart:carts) {
            ProductOrder order = new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(new Date());
            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());
            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();

            address.setFullName(orderRequest.getFullName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNo(orderRequest.getMobileNo());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setPostCode(orderRequest.getPostCode());

            order.setOrderAddress(address);

            productOrderRepository.save(order);

        }
    }

}
