package com.sakibee.controller;

import com.sakibee.model.Category;
import com.sakibee.model.Product;
import com.sakibee.service.CategoryService;
import com.sakibee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/products")
    public String products(Model m, @RequestParam(value="category", defaultValue = "") String category) {
        List<Category> categories = categoryService.getAllActiveCategory();
        m.addAttribute("categories", categories);
        List<Product> products = productService.getAllActiveProduct(category);
        m.addAttribute("products", products);
        m.addAttribute("paramValue", category);
        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable int id, Model m) {
        Product product = productService.getProduct(id);
        m.addAttribute("product", product);
        return "view_product";
    }


}
