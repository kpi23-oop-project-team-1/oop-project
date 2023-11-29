package com.mkr.server.domain;

import com.mkr.server.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;

public class Product implements Commentable {
    private int productId;
    private int traderId;
    private String title;
    private String[] imageSources;
    private int price;
    private int amount;
    private String description;
    private Comment[] comments;
    private ProductCategory category;
    private ProductState state;
    private ColorId color;
    private ProductStatus status;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getTraderId() {
        return traderId;
    }

    public void setTraderId(int traderId) {
        this.traderId = traderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getImageSources() {
        return imageSources;
    }

    public void setImageSources(String[] imageSources) {
        this.imageSources = imageSources;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Comment[] getComments() {
        return comments;
    }

    public void setComments(Comment[] comments) {
        this.comments = comments;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public ProductState getState() {
        return state;
    }

    public void setState(ProductState state) {
        this.state = state;
    }

    public ColorId getColor() {
        return color;
    }

    public void setColor(ColorId color) {
        this.color = color;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    @NotNull
    public Product copy() {
        var p = new Product();
        p.setProductId(productId);
        p.setTraderId(traderId);
        p.setTitle(title);
        p.setImageSources(imageSources);
        p.setPrice(price);
        p.setAmount(amount);
        p.setDescription(description);
        p.setComments(comments);
        p.setCategory(category);
        p.setState(state);
        p.setColor(color);
        p.setStatus(status);

        return p;
    }

    @NotNull
    public Product withComment(@NotNull Comment comment) {
        Product p = copy();
        p.setComments(ArrayUtils.withAddedElement(comments, comment));

        return p;
    }
}