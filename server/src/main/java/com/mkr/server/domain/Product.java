package com.mkr.server.domain;

import com.mkr.server.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class Product implements Commentable {
    private Integer productId;
    private Integer traderId;
    private String title;
    private Integer price;
    private Integer amount;
    private String description = "";
    private Integer[] comments;
    private ProductCategory category;
    private ProductState state;
    private ColorId color;
    private ProductStatus status;

    public Product() {
    }

    public Product(
        Integer productId,
        Integer traderId,
        String title,
        Integer price,
        Integer amount,
        ProductCategory category,
        ProductState state,
        ProductStatus status,
        ColorId color
    ) {
        this.productId = productId;
        this.traderId = traderId;
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.category = category;
        this.state = state;
        this.status = status;
        this.color = color;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getTraderId() {
        return traderId;
    }

    public void setTraderId(Integer traderId) {
        this.traderId = traderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Integer[] getComments() {
        return comments;
    }

    public void setComments(Integer[] comments) {
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
    public Product withComment(@NotNull Integer comment) {
        Product p = copy();
        p.setComments(ArrayUtils.withAddedElement(comments, comment));

        return p;
    }

    @NotNull
    public Product withStatus(@NotNull ProductStatus status) {
        Product p = copy();
        p.setStatus(status);

        return p;
    }

    @NotNull
    public Product withAmountAndStatus(int amount) {
        Product p = copy();
        p.setAmount(amount);
        if (amount == 0) {
            p.setStatus(ProductStatus.SOLD);
        }

        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (!Objects.equals(productId, product.productId)) return false;
        if (!Objects.equals(traderId, product.traderId)) return false;
        if (!Objects.equals(price, product.price)) return false;
        if (!Objects.equals(amount, product.amount)) return false;
        if (!title.equals(product.title)) return false;
        if (!description.equals(product.description)) return false;
        if (!Arrays.equals(comments, product.comments)) return false;
        if (category != product.category) return false;
        if (state != product.state) return false;
        if (color != product.color) return false;

        return status == product.status;
    }

    @Override
    public int hashCode() {
        int result = productId;
        result = 31 * result + traderId;
        result = 31 * result + title.hashCode();
        result = 31 * result + price;
        result = 31 * result + amount;
        result = 31 * result + description.hashCode();
        result = 31 * result + Arrays.hashCode(comments);
        result = 31 * result + category.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + color.hashCode();
        result = 31 * result + status.hashCode();

        return result;
    }
}