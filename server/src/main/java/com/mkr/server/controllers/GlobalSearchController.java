package com.mkr.server.controllers;

import com.mkr.server.common.IntRange;
import com.mkr.server.domain.ColorId;
import com.mkr.server.domain.Product;
import com.mkr.server.domain.ProductCategory;
import com.mkr.server.domain.ProductState;
import com.mkr.server.dto.ConciseProduct;
import com.mkr.server.dto.GlobalSearchResultDto;
import com.mkr.server.search.GlobalSearchFilter;
import com.mkr.server.search.GlobalSearchFilterDescription;
import com.mkr.server.search.SearchOrder;
import com.mkr.server.services.GlobalProductSearchService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Set;

@RestController
public class GlobalSearchController {
    @Autowired
    private GlobalProductSearchService service;

    @GetMapping("/api/searchfilterdesc")
    public GlobalSearchFilterDescription searchFilterDescription(
        @RequestParam(value = "category", required = false) ProductCategory category
    ) {
        return service.getSearchFilterDescription(category);
    }

    @GetMapping("/api/products")
    public GlobalSearchResultDto getProducts(
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "query", required = false) String query,
        @RequestParam(value = "order", required = false) SearchOrder order,
        @RequestParam(value = "category", required = false) ProductCategory category,
        @RequestParam(value = "price", required = false) IntRange priceRange,
        @RequestParam(value = "colors", required = false) ColorId[] colors,
        @RequestParam(value = "states", required = false) ProductState[] states
    ) {
        var filter = new GlobalSearchFilter(
            page == null ? 1 : page,
            query,
            category,
            order == null ? SearchOrder.CHEAPEST : order,
            priceRange,
            colors == null ? Set.of() : Set.of(colors),
            states == null ? Set.of() : Set.of(states)
        );

        var result = service.search(filter);
        var conciseProducts = Arrays.stream(result.products())
            .map(GlobalSearchController::toConciseProduct)
            .toArray(ConciseProduct[]::new);

        return new GlobalSearchResultDto(result.pageCount(), result.totalProductCount(), conciseProducts);
    }

    @NotNull
    private static ConciseProduct toConciseProduct(@NotNull Product product) {
        return new ConciseProduct(
            product.getProductId(),
            product.getTitle(),
            product.getImageSources()[0],
            product.getPrice(),
            product.getAmount()
        );
    }

}
