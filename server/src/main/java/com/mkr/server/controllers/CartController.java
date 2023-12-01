package com.mkr.server.controllers;

import com.mkr.server.dto.CartProductInfo;
import com.mkr.server.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/api/cartproducts")
    public CartProductInfo[] cartProducts(Authentication auth) {
        return cartService.getCartProducts(auth.getName());
    }

    @PostMapping("/api/addcartproduct")
    public void addCartProduct(@RequestParam("id") int id, Authentication auth) {
        cartService.addCartProduct(auth.getName(), id);
    }

    @PostMapping("/api/cartproductamount")
    public void cartProductAmount(@RequestParam("id") int id, @RequestParam("amount") int amount, Authentication auth) {
        cartService.changeCartProductAmount(auth.getName(), id, amount);
    }

    @DeleteMapping("/api/cartproduct")
    public void deleteCartProduct(@RequestParam("id") int id, Authentication auth) {
        cartService.removeCartProduct(auth.getName(), id);
    }
}
