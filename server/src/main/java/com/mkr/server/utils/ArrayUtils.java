package com.mkr.server.utils;

import java.util.Arrays;

public class ArrayUtils {
    public static<T> T[] withAddedElement(T[] elements, T element) {
        T[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[elements.length] = element;

        return newArray;
    }
}
