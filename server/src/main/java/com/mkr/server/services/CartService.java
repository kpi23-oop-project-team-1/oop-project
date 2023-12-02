package com.mkr.server.services;

import com.mkr.server.domain.CartProduct;
import com.mkr.server.domain.CustomerTraderUser;
import com.mkr.server.domain.Product;
import com.mkr.server.domain.ProductStatus;
import com.mkr.server.dto.CartProductInfo;
import com.mkr.server.repositories.ProductRepository;
import com.mkr.server.repositories.UserRepository;
import com.mkr.server.services.storage.ProductImageUrlMapper;
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

    @Autowired
    private ProductImageUrlMapper imageUrlMapper;

    @NotNull
    public CartProductInfo[] getCartProducts(@NotNull String userEmail) {
        CustomerTraderUser user = findUser(userEmail);
        CartProduct[] cartProducts = user.getCartProducts();

        return Arrays.stream(cartProducts).map(this::convertToCartProductInfo).toArray(CartProductInfo[]::new);
    }

    public void addCartProduct(@NotNull String userEmail, int productId) {
        CustomerTraderUser user = findUser(userEmail);
        Product product = productRepo.getProductById(productId).orElseThrow();

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ValidationException("Invalid status");
        }

        if (product.getTraderId() == user.getId()) {
            throw new ValidationException("Cannot add to cart own product");
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
        Product product = productRepo.getProductById(cartProduct.getProductId()).orElseThrow();
        String imageSource = imageUrlMapper.getEndpointUrl(product.getProductId(), 0);

        return new CartProductInfo(
            product.getProductId(),
            product.getTitle(),
            imageSource,
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
