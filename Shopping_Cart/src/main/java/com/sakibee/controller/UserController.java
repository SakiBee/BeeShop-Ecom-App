package com.sakibee.controller;

import com.sakibee.model.Cart;
import com.sakibee.model.OrderRequest;
import com.sakibee.model.User;
import com.sakibee.service.CartService;
import com.sakibee.service.ProductOrderService;
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
    @Autowired
    private ProductOrderService productOrderService;

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
        User user = getLoggedInUser(p);
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
    public String OrderPage(Principal p, Model m) {
        User user = getLoggedInUser(p);
        List<Cart> carts = cartService.getCartByUser(user.getId());
        m.addAttribute("carts", carts);

        Double totalOrderPrice = 0.0, orderPrice = 0.0;
        Double tax = 100.0;
        Double shipping_fee = 150.0;
        if(carts.size() > 0) {
            orderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
            totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice()+tax+shipping_fee;
        }
        m.addAttribute("tax", tax);
        m.addAttribute("orderPrice", orderPrice);
        m.addAttribute("totalOrderPrice", totalOrderPrice);
        return "/user/order";
    }

    private User getLoggedInUser (Principal p) {
        return userService.getUserByEmail(p.getName());
    }

    @PostMapping("/saveOrder")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest, Principal p) {
        User user = getLoggedInUser(p);
        productOrderService.saveOrder(user.getId(), orderRequest);
        return "/user/success";
    }

    @GetMapping("/profile")
    public String profile() {
        return "/user/profile";
    }


}
