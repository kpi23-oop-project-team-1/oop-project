package com.mkr.server.dto;

import org.springframework.web.multipart.MultipartFile;

public record UpdateProductInfo(
        String title,
        String description,
        int price,
        int amount,
        String category,
        String state,
        String color,
        int id,
        MultipartFile[] image
) {
}
