package com.mkr.server.domain;

public class CartProduct {
    private int productId;
    private int quantity;

    public CartProduct() {
    }

    public CartProduct(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CartProduct other &&
            productId == other.productId &&
            quantity == other.quantity;
    }

    @Override
    public int hashCode() {
        int result = productId;
        result = 31 * result + quantity;

        return result;
    }
}
