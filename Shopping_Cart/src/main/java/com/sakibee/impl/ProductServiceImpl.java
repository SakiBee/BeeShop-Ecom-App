package com.sakibee.impl;

import com.sakibee.model.Product;
import com.sakibee.repository.ProductRepository;
import com.sakibee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> searchProduct(String text) {
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(text, text);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products;
    }

    @Override
    public Product getProduct(int id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    @Override
    public Product updateProduct(Product product, MultipartFile file) {

        Product oldProduct = getProduct(product.getId());
        String imageName = file.isEmpty() ? oldProduct.getImage() : file.getOriginalFilename();
        double discount = product.getPrice() - (product.getPrice() * product.getDiscount() * 0.01);

        oldProduct.setTitle(product.getTitle());
        oldProduct.setCategory(product.getCategory());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setDiscount(product.getDiscount());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setStock(product.getStock());
        oldProduct.setImage(imageName);
        oldProduct.setIsActive(product.getIsActive());
        oldProduct.setDiscountPrice(discount);

        Product updatedProduct = productRepository.save(oldProduct);

        if(!ObjectUtils.isEmpty(updatedProduct)) {
            if(!file.isEmpty()) {
                try {
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + file.getOriginalFilename());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return updatedProduct;
        }
        return null;
    }

    @Override
    public Boolean deleteById(int id) {
        Product product = productRepository.findById(id).orElse(null);
        if(!ObjectUtils.isEmpty(product)) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public List<Product> getAllActiveProduct(String category) {
        List<Product> products = null;
        if(ObjectUtils.isEmpty(category)) {
            products = productRepository.findByIsActiveTrue();
        } else {
            products = productRepository.findByCategory(category);
        }
        return products;
    }






}
