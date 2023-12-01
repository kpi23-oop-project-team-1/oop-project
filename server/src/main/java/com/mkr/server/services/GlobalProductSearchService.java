package com.mkr.server.services;

import com.mkr.server.common.IntRange;
import com.mkr.server.domain.Product;
import com.mkr.server.domain.ProductCategory;
import com.mkr.server.domain.ProductStatus;
import com.mkr.server.repositories.ProductRepository;
import com.mkr.server.search.GlobalSearchFilter;
import com.mkr.server.search.GlobalSearchFilterDescription;
import com.mkr.server.search.GlobalSearchResult;
import com.mkr.server.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.function.Predicate;

@Service
public class GlobalProductSearchService {
    private static final int PAGE_SIZE = 20;

    @Autowired
    private ProductRepository repo;

    @NotNull
    public GlobalSearchFilterDescription getSearchFilterDescription(@Nullable ProductCategory category) {
        Predicate<Product> productPredicate = p ->
            (category == null || p.getCategory().equals(category)) &&
            p.getStatus().equals(ProductStatus.ACTIVE);

        return repo.collectIf(productPredicate, GlobalSearchFilterDescription.productConsumer());
    }

    @NotNull
    public GlobalSearchResult search(@NotNull GlobalSearchFilter filter) {
        var sampleRange = IntRange.fixedStep(filter.page() - 1, PAGE_SIZE);
        Predicate<Product> predicate = getFilterPredicate(filter);

        int totalProductCount = repo.countProducts(predicate);
        int pageCount = MathUtils.ceilDiv(totalProductCount, PAGE_SIZE);
        Product[] products = repo.filterProducts(predicate, filter.order(), sampleRange);

        return new GlobalSearchResult(pageCount, totalProductCount, products);
    }

    @NotNull
    private static Predicate<Product> getFilterPredicate(@NotNull GlobalSearchFilter filter) {
        String lowerQuery = filter.query() != null ? filter.query().toLowerCase(Locale.ROOT) : null;

        return product -> {
            if (!product.getStatus().equals(ProductStatus.ACTIVE)) {
                return false;
            }

            if (lowerQuery != null && !product.getTitle().toLowerCase(Locale.ROOT).equals(lowerQuery)) {
                return false;
            }

            if (!filter.states().isEmpty() && !filter.states().contains(product.getState())) {
                return false;
            }

            if (!filter.colorIds().isEmpty() && !filter.colorIds().contains(product.getColor())) {
                return false;
            }

            if (filter.category() != null && !filter.category().equals(product.getCategory())) {
                return false;
            }

            return filter.priceRange() == null || filter.priceRange().contains(product.getPrice());
        };
    }
}
