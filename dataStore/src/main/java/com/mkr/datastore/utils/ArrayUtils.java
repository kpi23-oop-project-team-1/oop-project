package com.mkr.datastore.utils;

import java.util.Objects;

public final class ArrayUtils {
    private ArrayUtils() {}

    public static <T> int indexOf(T[] values, T target) {
        for (int i = 0; i < values.length; i++) {
            if (Objects.equals(values[i], target)) {
                return i;
            }
        }

        return -1;
    }
}
