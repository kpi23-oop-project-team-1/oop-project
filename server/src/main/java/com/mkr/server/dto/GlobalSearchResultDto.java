package com.mkr.server.dto;

public record GlobalSearchResultDto(
    int pageCount,
    int totalProductCount,
    ConciseProduct[] products
) {
}
