package com.mkr.server.domain;

import java.util.Objects;

public class CartProduct {
    private Integer productId;
    private Integer quantity;

    public CartProduct() {
    }

    public CartProduct(Integer productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CartProduct other &&
                Objects.equals(productId, other.productId) &&
                Objects.equals(quantity, other.quantity);
    }

    @Override
    public int hashCode() {
        int result = productId;
        result = 31 * result + quantity;

        return result;
    }
}
