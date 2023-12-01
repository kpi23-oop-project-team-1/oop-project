package com.mkr.server.services;

import com.mkr.server.domain.CartProduct;
import com.mkr.server.domain.CustomerTraderUser;
import com.mkr.server.domain.Product;
import com.mkr.server.dto.CartProductInfo;
import com.mkr.server.repositories.ProductRepository;
import com.mkr.server.repositories.UserRepository;
import jakarta.validation.ValidationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CartService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository productRepo;

    @NotNull
    public CartProductInfo[] getCartProducts(@NotNull String userEmail) {
        CustomerTraderUser user = findUser(userEmail);
        CartProduct[] cartProducts = user.getCartProducts();

        return Arrays.stream(cartProducts).map(this::convertToCartProductInfo).toArray(CartProductInfo[]::new);
    }

    public void addCartProduct(@NotNull String userEmail, int productId) {
        CustomerTraderUser user = findUser(userEmail);

        if (!productRepo.containsProductWithId(productId)) {
            throw new ValidationException("productId");
        }

        userRepo.addCartProduct(user.getId(), new CartProduct(productId, 1));
    }

    public void changeCartProductAmount(@NotNull String email, int productId, int newAmount) {
        CustomerTraderUser user = findUser(email);
        Product product = productRepo.getProductById(productId)
            .orElseThrow(() -> new IllegalArgumentException("productId"));

        if (newAmount > product.getAmount()) {
            throw new ValidationException("newAmount");
        }

        userRepo.updateCartProductAmount(user.getId(), productId, newAmount);
    }

    public void removeCartProduct(@NotNull String email, int productId) {
        CustomerTraderUser user = findUser(email);

        userRepo.removeCartProduct(user.getId(), productId);
    }

    @NotNull
    private CartProductInfo convertToCartProductInfo(@NotNull CartProduct cartProduct) {
        Product product = productRepo.getProductById(cartProduct.getProductId()).get();

        return new CartProductInfo(
            product.getProductId(),
            product.getTitle(),
            product.getImageSources()[0],
            product.getPrice(),
            cartProduct.getQuantity(),
            product.getAmount()
        );
    }

    private CustomerTraderUser findUser(@NotNull String email) {
        return userRepo.findCustomerTraderUserByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
