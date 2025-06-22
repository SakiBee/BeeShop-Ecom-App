package com.sakibee.service;

import com.sakibee.model.Product;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);
    public List<Product> getAllProducts();
    public Product getProduct(int id);
    public Boolean deleteById(int id);
}
