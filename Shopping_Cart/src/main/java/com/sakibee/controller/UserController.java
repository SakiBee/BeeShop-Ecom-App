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
import java.util.List;

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
            Integer productCount = cartService.getCartProductCount(user.getId());
            m.addAttribute("productCount",productCount);
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

    @GetMapping("/cart")
    public String laodCart(Principal p, Model m) {
        User user = userService.getUserByEmail(p.getName());
        List<Cart> carts = cartService.getCartByUser(user.getId());
        Double totalOrderPrice = 0.0;
        if(carts.size() > 0) {
            totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
        }
        m.addAttribute("carts", carts);
        m.addAttribute("totalOrderPrice", totalOrderPrice);

        return "/user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sym, @RequestParam Integer cid) {
        cartService.updateCartQuantity(cid, sym);
        return "redirect:/user/cart";
    }

    @GetMapping("/orders")
    public String OrderPage() {
        return "/user/order";
    }


}
