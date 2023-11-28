package com.mkr.server.search;

import com.mkr.server.common.IntRange;
import com.mkr.server.domain.ColorId;
import com.mkr.server.domain.ProductState;

import java.util.Arrays;

public record GlobalSearchFilterDescription(
    IntRange limitingPriceRange,
    ColorId[] colorIds,
    ProductState[] states
) {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof GlobalSearchFilterDescription desc &&
            limitingPriceRange.equals(desc.limitingPriceRange) &&
            Arrays.equals(colorIds, desc.colorIds) &&
            Arrays.equals(states, desc.states);
    }

    @Override
    public int hashCode() {
        int result = limitingPriceRange.hashCode();
        result = 31 * result + Arrays.hashCode(colorIds);
        result = 31 * result + Arrays.hashCode(states);

        return result;
    }
}
