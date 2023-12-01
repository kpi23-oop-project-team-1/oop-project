package com.mkr.server.domain;

import com.mkr.datastore.InheritedModel;

import java.util.Arrays;

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
        u.setComments(comments);
        u.setProducts(products);
        u.setCartProducts(cartProducts);

        return u;
    }

    public CustomerTraderUser withComment(Comment comment) {
        CustomerTraderUser u = copy();
        var newComments = Arrays.copyOf(u.comments, u.comments.length + 1);
        newComments[u.comments.length] = comment;

        u.setComments(newComments);

        return u;
    }
}
