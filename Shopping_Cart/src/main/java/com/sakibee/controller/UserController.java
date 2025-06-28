package com.sakibee.controller;

import com.sakibee.model.Cart;
import com.sakibee.model.User;
import com.sakibee.service.CartService;
import com.sakibee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void getUserDatails(Principal p, Model m) {
        if(p != null) {
            String email = p.getName();
            User user = userService.getUserByEmail(email);
            m.addAttribute("user", user);
        }
    }
    public String home() {
        return "user/home";
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, RedirectAttributes redirectAttributes) {
        Cart cart = cartService.saveCart(pid, uid);
        if(ObjectUtils.isEmpty(cart)) {
            redirectAttributes.addFlashAttribute("errorMsg", "Internal Error");
        } else redirectAttributes.addFlashAttribute("successMsg", "Product added to cart successfully");

        return "redirect:/product/"+pid;
    }


}
