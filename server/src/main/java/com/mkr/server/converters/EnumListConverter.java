package com.mkr.server.converters;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

public abstract class EnumListConverter<T> implements Converter<String, T[]> {
    @Override
    public T[] convert(@NotNull String source) {
        String[] rawElements = source.split(",");
        T[] elements = createArray(rawElements.length);

        for (int i = 0; i < rawElements.length; i++) {
            elements[i] = fromText(rawElements[i]);
        }

        return elements;
    }

    @NotNull
    protected abstract T[] createArray(int size);

    @NotNull
    protected abstract T fromText(@NotNull String text);
}
