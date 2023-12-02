package com.mkr.server.dto;

public record ProductInfo(
    int id,
    String title,
    String[] imageSources,
    String stripeText,
    int price,
    int totalAmount,
    String description,
    CommentInfo[] comments,
    String category,
    String state,
    String color
) {
}
