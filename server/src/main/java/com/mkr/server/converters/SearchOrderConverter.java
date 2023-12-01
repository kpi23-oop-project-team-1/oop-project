package com.mkr.server.converters;

import com.mkr.server.search.SearchOrder;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

public class SearchOrderConverter implements Converter<String, SearchOrder>  {
    @Override
    public SearchOrder convert(@NotNull String source) {
        return SearchOrder.forValue(source);
    }
}
