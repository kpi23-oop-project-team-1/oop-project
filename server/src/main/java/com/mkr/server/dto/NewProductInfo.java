package com.mkr.server.dto;

import org.springframework.web.multipart.MultipartFile;

public record NewProductInfo(
    String title,
    String description,
    int price,
    int amount,
    String category,
    String state,
    String color,
    MultipartFile[] image
) {
}
