package com.mkr.server.converters;

import com.mkr.server.common.IntRange;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IntRangeConverter implements Converter<String, IntRange> {
    @Override
    public IntRange convert(@NotNull String source) {
        return IntRange.parse(source);
    }
}
