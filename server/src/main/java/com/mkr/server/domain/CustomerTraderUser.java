package com.mkr.server.domain;

import com.mkr.datastore.InheritedModel;
import com.mkr.server.dto.CartProductInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@InheritedModel(id = "customer_trader")
public class CustomerTraderUser extends User implements Commentable {
    private String pfpSource;
    private String displayName;

    private String firstName;
    private String lastName;

    private String telNumber;
    private String profileDescription;

    private Comment[] comments;
    private Product[] products;
    private CartProduct[] cartProducts;

    public CustomerTraderUser() {
    }

    public CustomerTraderUser(int id, String email, String passwordHash) {
        super(id, email, passwordHash);
    }

    @Override
    public UserRole getRole() {
        return UserRole.CUSTOMER_TRADER;
    }

    @Override
    public Comment[] getComments() {
        return comments;
    }

    public void setComments(Comment[] comments) {
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

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public CartProduct[] getCartProducts() {
        return cartProducts;
    }

    public void setCartProducts(CartProduct[] cartProducts) {
        this.cartProducts = cartProducts;
    }

    public String getPfpSource() {
        return pfpSource;
    }

    public void setPfpSource(String pfpSource) {
        this.pfpSource = pfpSource;
    }

    public CustomerTraderUser copy() {
        var u = new CustomerTraderUser(getId(), getEmail(), getPasswordHash());

        u.setPfpSource(pfpSource);
        u.setDisplayName(displayName);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setTelNumber(telNumber);
        u.setProfileDescription(profileDescription);
        u.setComments(Arrays.copyOf(comments, comments.length));
        u.setProducts(Arrays.copyOf(products, products.length));
        u.setCartProducts(Arrays.copyOf(cartProducts, cartProducts.length));

        return u;
    }

    public CustomerTraderUser withComment(Comment comment) {
        CustomerTraderUser u = copy();
        var newComments = Arrays.copyOf(u.comments, u.comments.length + 1);
        newComments[u.comments.length] = comment;

        u.setComments(newComments);

        return u;
    }

    public CustomerTraderUser withPersonalInfo(String passwordHash, String firstName, String lastName, String telNumber) {
        CustomerTraderUser u = copy();

        u.setPasswordHash(passwordHash);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setTelNumber(telNumber);

        return u;
    }

    public CustomerTraderUser withCartProduct(CartProduct cartProduct) {
        CustomerTraderUser u = copy();
        var newCartProducts = Arrays.copyOf(u.cartProducts, u.cartProducts.length + 1);
        newCartProducts[u.cartProducts.length] = cartProduct;

        u.setCartProducts(newCartProducts);

        return u;
    }

    public CustomerTraderUser withUpdatedCartProductAmount(int productId, int newAmount) {
        CustomerTraderUser u = copy();
        CartProduct[] cartProducts = u.cartProducts;

        for (CartProduct cartProduct : cartProducts) {
            if (cartProduct.getProductId() == productId) {
                cartProduct.setQuantity(newAmount);
                break;
            }
        }

        return u;
    }

    public CustomerTraderUser withRemovedCartProduct(int productId) {
        CustomerTraderUser u = copy();
        List<CartProduct> productList = new ArrayList<>(List.of(u.cartProducts));
        productList.removeIf(p -> p.getProductId() == productId);
        u.cartProducts = productList.toArray(CartProduct[]::new);

        return u;
    }

}
