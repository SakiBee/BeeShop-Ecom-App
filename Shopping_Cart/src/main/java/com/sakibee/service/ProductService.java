package com.sakibee.service;

import com.sakibee.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);
    public List<Product> getAllProducts();
    public Product getProduct(int id);
    public Boolean deleteById(int id);
    public Product updateProduct(Product product, MultipartFile file);
    public List<Product> getAllActiveProduct(String category);
    public List<Product> searchProduct(String text);
}
