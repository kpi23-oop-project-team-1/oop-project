package com.mkr.server.dto;

import com.mkr.server.domain.ProductStatus;

public record ProductInfo(
    int id,
    String title,
    String[] imageSources,
    int price,
    int totalAmount,
    String description,
    CommentInfo[] comments,
    String category,
    String state,
    String color,
    ProductStatus status,
    ConciseUserInfo trader
) {
}
