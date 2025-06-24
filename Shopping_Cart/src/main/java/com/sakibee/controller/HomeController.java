package com.sakibee.controller;

import com.sakibee.model.Category;
import com.sakibee.model.Product;
import com.sakibee.model.User;
import com.sakibee.service.CategoryService;
import com.sakibee.service.ProductService;
import com.sakibee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/signin")
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


    //User
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        String imageName = file.isEmpty() ? "defaultImage.jpg" : file.getOriginalFilename();
        user.setImage(imageName);
        User saveUser = userService.saveUser(user);
        if(!ObjectUtils.isEmpty(saveUser)) {
            if(!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            redirectAttributes.addFlashAttribute("successMsg", "Congratulations! You have Successfully Registered");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Oops! Something is wrong");
        }
        return "redirect:/register";
    }


}
