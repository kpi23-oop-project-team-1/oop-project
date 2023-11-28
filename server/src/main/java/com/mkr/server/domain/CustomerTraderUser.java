package com.mkr.server.domain;

public class CustomerTraderUser extends User implements Commentable {
    private String displayName;

    private String firstName;
    private String lastName;

    private String telNumber;
    private String profileDescription;

    private Comment[] comments;
    private Product[] products;
    private CartProduct[] cartProducts;

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
}
