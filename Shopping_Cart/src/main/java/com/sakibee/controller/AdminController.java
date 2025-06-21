package com.sakibee.controller;

import com.sakibee.model.Category;
import com.sakibee.service.CategoryService;
import jakarta.servlet.http.HttpSession;
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

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String index() { return "admin/index";}
    @GetMapping("/add_product")
    public String addProduct() { return "admin/add_product";}
    @GetMapping("/category")
    public String category(Model m) {
        m.addAttribute("categorys", categoryService.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {

        String imageName = file.getOriginalFilename();
        if(imageName.isEmpty()) imageName = "defaultImage.jpg";
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

    @GetMapping("/editCategory/{id}")
    public String editCategory(@PathVariable int id, Model m, RedirectAttributes redirectAttributes) {
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
        return "redirect:/admin/editCategory/"+category.getId();
    }
}
