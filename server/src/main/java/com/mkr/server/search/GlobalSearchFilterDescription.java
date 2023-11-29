package com.mkr.server.search;

import com.mkr.server.common.IntRange;
import com.mkr.server.domain.ColorId;
import com.mkr.server.domain.Product;
import com.mkr.server.domain.ProductState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public record GlobalSearchFilterDescription(
    IntRange limitingPriceRange,
    Set<ColorId> colorIds,
    Set<ProductState> states
) {
    private static final class ProductCollectorState {
        @Nullable
        public IntRange limitingPriceRange;

        public HashSet<ColorId> colorIds = new HashSet<>();
        public HashSet<ProductState> states = new HashSet<>();
    }

    @NotNull
    public static Collector<Product, ?, GlobalSearchFilterDescription> productConsumer() {
        return new Collector<Product, ProductCollectorState, GlobalSearchFilterDescription>() {
            @Override
            public Supplier<ProductCollectorState> supplier() {
                return ProductCollectorState::new;
            }

            @Override
            public BiConsumer<ProductCollectorState, Product> accumulator() {
                return (state, product) -> {
                    int price = product.getPrice();

                    if (state.limitingPriceRange == null) {
                        state.limitingPriceRange = new IntRange(price, price);
                    } else {
                        state.limitingPriceRange = state.limitingPriceRange.widen(price);
                    }

                    state.colorIds.add(product.getColor());
                    state.states.add(product.getState());
                };
            }

            @Override
            public BinaryOperator<ProductCollectorState> combiner() {
                return (s1, s2) -> {
                    var s = new ProductCollectorState();

                    s.limitingPriceRange = IntRange.union(s1.limitingPriceRange, s2.limitingPriceRange);
                    s.states.addAll(s1.states);
                    s.states.addAll(s2.states);
                    s.colorIds.addAll(s1.colorIds);
                    s.colorIds.addAll(s2.colorIds);

                    return s;
                };
            }

            @Override
            public Function<ProductCollectorState, GlobalSearchFilterDescription> finisher() {
                return state -> new GlobalSearchFilterDescription(
                    state.limitingPriceRange,
                    state.colorIds,
                    state.states
                );
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of();
            }
        };
    }
}
