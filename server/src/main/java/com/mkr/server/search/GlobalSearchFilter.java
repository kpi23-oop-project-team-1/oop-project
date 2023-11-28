package com.mkr.server.search;

import com.mkr.server.common.IntRange;
import com.mkr.server.domain.ColorId;
import com.mkr.server.domain.ProductCategory;
import com.mkr.server.domain.ProductState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public record GlobalSearchFilter(
    int page,
    @Nullable String query,
    @Nullable ProductCategory category,
    SearchOrder order,
    @Nullable IntRange priceRange,
    ColorId[] colorIds,
    ProductState[] states
) {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof GlobalSearchFilter filter &&
            page == filter.page &&
            Objects.equals(query, filter.query) &&
            Objects.equals(category, filter.category) &&
            order.equals(filter.order) &&
            Objects.equals(priceRange, filter.priceRange) &&
            Arrays.equals(colorIds, filter.colorIds) &&
            Arrays.equals(states, filter.states);
    }

    @Override
    public int hashCode() {
        int result = page;
        result = result * 31 + (query == null ? 0 : query.hashCode());
        result = result * 31 + (category == null ? 0 : category.ordinal());
        result = result * 31 + order.ordinal();
        result = result * 31 + (priceRange == null ? 0 : priceRange.hashCode());
        result = result * 31 + Arrays.hashCode(colorIds);
        result = result * 31 + Arrays.hashCode(states);

        return result;
    }
}