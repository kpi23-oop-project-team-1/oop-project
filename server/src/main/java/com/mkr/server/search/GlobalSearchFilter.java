package com.mkr.server.search;

import com.mkr.server.common.IntRange;
import com.mkr.server.domain.ColorId;
import com.mkr.server.domain.ProductCategory;
import com.mkr.server.domain.ProductState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record GlobalSearchFilter(
    int page,
    @Nullable String query,
    @Nullable ProductCategory category,
    @NotNull SearchOrder order,
    @Nullable IntRange priceRange,
    @NotNull Set<ColorId> colorIds,
    @NotNull Set<ProductState> states
) {
}