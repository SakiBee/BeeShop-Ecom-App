package com.sakibee.service;

import com.sakibee.model.OrderRequest;
import com.sakibee.model.ProductOrder;

public interface ProductOrderService {
    public void saveOrder(Integer userId, OrderRequest orderRequest);
}
