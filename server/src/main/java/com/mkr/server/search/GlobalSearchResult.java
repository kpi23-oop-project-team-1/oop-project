package com.mkr.server.search;

import com.mkr.server.domain.Product;

public record GlobalSearchResult(
    int pageCount,
    int totalProductCount,
    Product[] products
) {
}
