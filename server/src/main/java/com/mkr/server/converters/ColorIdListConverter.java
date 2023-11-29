package com.mkr.server.converters;

import com.mkr.server.domain.ColorId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ColorIdListConverter extends EnumListConverter<ColorId> {
    @Override
    protected ColorId[] createArray(int size) {
        return new ColorId[size];
    }

    @Override
    @NotNull
    protected ColorId fromText(@NotNull String text) {
        return ColorId.forValue(text);
    }
}
