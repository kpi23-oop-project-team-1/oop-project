package com.mkr.server.domain;

import com.mkr.datastore.InheritedModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@InheritedModel(id = "customer_trader")
public class CustomerTraderUser extends User implements Commentable {
    private String displayName;

    private String firstName;
    private String lastName;

    private String telNumber;
    private String profileDescription;

    private Integer[] comments;
    private Integer[] cartProductIds;
    private Integer[] cartProductQuantities;

    public CustomerTraderUser() {
    }

    public CustomerTraderUser(Integer id, String email, String passwordHash) {
        super(id, email, passwordHash);
    }

    @Override
    public UserRole getRole() {
        return UserRole.CUSTOMER_TRADER;
    }

    @Override
    public Integer[] getComments() {
        return comments;
    }

    public void setComments(Integer[] comments) {
        this.comments = comments;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public Integer[] getCartProductIds() {
        return cartProductIds;
    }

    public void setCartProductIds(Integer[] cartProductIds) {
        this.cartProductIds = cartProductIds;
    }

    public Integer[] getCartProductQuantities() {
        return cartProductQuantities;
    }

    public void setCartProductQuantities(Integer[] cartProductQuantities) {
        this.cartProductQuantities = cartProductQuantities;
    }

    public CartProduct[] getCartProducts() {
        var cartProducts = new CartProduct[cartProductIds.length];

        Arrays.setAll(
                cartProducts,
                i -> new CartProduct(cartProductIds[i], cartProductQuantities[i])
        );

        return cartProducts;
    }

    public void setCartProducts(CartProduct[] cartProducts) {
        cartProductIds = new Integer[cartProducts.length];

        Arrays.setAll(
                cartProductIds,
                i -> cartProducts[i].getProductId()
        );

        cartProductQuantities = new Integer[cartProducts.length];

        Arrays.setAll(
                cartProductQuantities,
                i -> cartProducts[i].getQuantity()
        );
    }

    public CustomerTraderUser copy() {
        var u = new CustomerTraderUser(getId(), getEmail(), getPasswordHash());

        u.setDisplayName(displayName);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setTelNumber(telNumber);
        u.setProfileDescription(profileDescription);
        u.setComments(Arrays.copyOf(comments, comments.length));
        u.setCartProductIds(Arrays.copyOf(cartProductIds, cartProductIds.length));
        u.setCartProductQuantities(Arrays.copyOf(cartProductQuantities, cartProductQuantities.length));

        return u;
    }

    public CustomerTraderUser withComment(Integer comment) {
        CustomerTraderUser u = copy();
        var newComments = Arrays.copyOf(u.comments, u.comments.length + 1);
        newComments[u.comments.length] = comment;

        u.setComments(newComments);

        return u;
    }

    public CustomerTraderUser withPersonalInfo(
        @Nullable String passwordHash,
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull String description,
        @NotNull String displayName,
        @NotNull String telNumber
    ) {
        CustomerTraderUser u = copy();

        if (passwordHash != null) {
            u.setPasswordHash(passwordHash);
        }

        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setTelNumber(telNumber);
        u.setProfileDescription(description);
        u.setDisplayName(displayName);

        return u;
    }

    public CustomerTraderUser withCartProduct(CartProduct cartProduct) {
        CustomerTraderUser u = copy();

        var cartProducts = u.getCartProducts();
        var newCartProducts = Arrays.copyOf(cartProducts, cartProducts.length + 1);
        newCartProducts[cartProducts.length] = cartProduct;

        u.setCartProducts(newCartProducts);

        return u;
    }

    public CustomerTraderUser withUpdatedCartProductAmount(int productId, int newAmount) {
        CustomerTraderUser u = copy();
        var cartProducts = u.getCartProducts();

        for (CartProduct cartProduct : cartProducts) {
            if (cartProduct.getProductId() == productId) {
                cartProduct.setQuantity(newAmount);
                break;
            }
        }

        u.setCartProducts(cartProducts);

        return u;
    }

    public CustomerTraderUser withRemovedCartProduct(int productId) {
        CustomerTraderUser u = copy();
        var productList = new ArrayList<>(List.of(u.getCartProducts()));

        productList.removeIf(p -> p.getProductId() == productId);

        u.setCartProducts(productList.toArray(CartProduct[]::new));

        return u;
    }

}
