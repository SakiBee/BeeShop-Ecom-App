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
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    @ModelAttribute
    public void getUserDatails(Principal p, Model m) {
        if(p != null) {
            String email = p.getName();
            User user = userService.getUserByEmail(email);
            m.addAttribute("user", user);
        }
    }

    @GetMapping("/")
    public String index() { return "admin/index";}
    @GetMapping("/add_product")
    public String addProduct() { return "admin/add_product";}


    //Category

    @GetMapping("/category")
    public String category(Model m) {
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {

        String imageName = file.isEmpty() ? "defaultImage.jpg" : file.getOriginalFilename();
        category.setImageName(imageName);

        Boolean existCategory = categoryService.existCategory(category.getName());
        if(existCategory) {
            redirectAttributes.addFlashAttribute("errorMsg", "Category " + category.getName() + " is already exists.");
        } else {
            Category saveCategory = categoryService.saveCategory(category);
            if(!ObjectUtils.isEmpty(saveCategory)) {

                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                //handle missing field
                redirectAttributes.addFlashAttribute("successMsg", "Category " + category.getName() + " Successfully Saved");
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "Internal Server Error.");
            }
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}") //work of DeteleMapping
    public String deleteCategory(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Boolean deleteCategory = categoryService.deleteCategory(id);
        if(deleteCategory) redirectAttributes.addFlashAttribute("successMsg", "Category Successfully Deleted");
        else redirectAttributes.addFlashAttribute("errorMsg", "Internal Server Error");
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model m, RedirectAttributes redirectAttributes) {
        m.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }
    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        Category oldCategory = categoryService.getCategoryById(category.getId());
        String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();
        if(!ObjectUtils.isEmpty(category)) {
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(imageName);
        }
        Category updateCategory = categoryService.saveCategory(oldCategory);
        if(!ObjectUtils.isEmpty(updateCategory)) {
            if(!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            redirectAttributes.addFlashAttribute("successMsg", "Category Updated Successfully");
        }
        else redirectAttributes.addFlashAttribute("errorMsg", "Something is wrong");
//        return "redirect:/admin/loadEditCategory/"+category.getId(); //stay on the same page
        return "redirect:/admin/category";
    }



    //Product
    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m) {
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
        return "admin/add_product";
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {

        String imageName = file.isEmpty() ? "defaultImage.jpg" : file.getOriginalFilename();
        product.setImage(imageName);
        double dis_price = product.getPrice() - (product.getPrice() * product.getDiscount() * 0.01);
        product.setDiscountPrice(dis_price);
        Product saveProduct = productService.saveProduct(product);

        if(!ObjectUtils.isEmpty(saveProduct)) {
            File saveFile = new ClassPathResource("static/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + file.getOriginalFilename());
            System.out.println(path);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            redirectAttributes.addFlashAttribute("successMsg", "Product Added Successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Internal Server Error.");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model m) {
        List<Product> products = productService.getAllProducts();
        m.addAttribute("products", products);
        return "admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Boolean deleteProduct = productService.deleteById(id);
        if(deleteProduct) {
            redirectAttributes.addFlashAttribute("successMsg", "Product Deleted Successfully");
        } else redirectAttributes.addFlashAttribute("errorMsg", "Internal Server Error.");
        return "redirect:/admin/products";
    }

    @GetMapping("/loadEditProduct/{id}")
    public String loadEditProduct(@PathVariable int id, Model m) {
        Product product = productService.getProduct(id);
        m.addAttribute("product", product);
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateCategory(@ModelAttribute Product product, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        double discount = product.getDiscount();
        if(discount < 0 || discount > 100) {
            redirectAttributes.addFlashAttribute("errorMsg", "Invalid Discount.");
        }
        else {
            Product updateProduct = productService.updateProduct(product, file);
            if(!ObjectUtils.isEmpty(updateProduct)) {
                redirectAttributes.addFlashAttribute("successMsg", "Product Updated Successfully");
            } else redirectAttributes.addFlashAttribute("errorMsg", "Internal Server Error.");
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/users")
    public String loadViewUsers(Model m) {
        List<User> users = userService.getAllUsers("ROLE_USER");
        m.addAttribute("users", users);
        return "/admin/users";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Boolean deleteUser = userService.deleteById(id);
        if(deleteUser) {
            redirectAttributes.addFlashAttribute("successMsg", "User Deleted Successfully");
        }
        else redirectAttributes.addFlashAttribute("errorMsg", "Internal Error");
        return "redirect:/admin/users";
    }

    @GetMapping("/updateStatus")
    public String updateUserAccStatus(@RequestParam Integer id, @RequestParam Boolean status, RedirectAttributes redirectAttributes) {
        Boolean updated = userService.updateAccountStatus(id, status);
        if(updated) {
            redirectAttributes.addFlashAttribute("successMsg", "User Updated Successfully");
        }
        else redirectAttributes.addFlashAttribute("errorMsg", "Internal Error");

        return "redirect:/admin/users";
    }

}
