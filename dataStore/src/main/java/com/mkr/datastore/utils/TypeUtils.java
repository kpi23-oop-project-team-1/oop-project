package com.mkr.datastore.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class TypeUtils {
    @NotNull
    public static String createGetterName(@NotNull String valueName) {
        if (valueName.isEmpty()) {
            return "get";
        }

        // valueName => get[ValueName]
        // "number" => getNumber
        var sb = new StringBuilder();
        sb.append("get");
        sb.appendCodePoint(Character.toUpperCase(valueName.codePointAt(0)));
        sb.append(valueName,1, valueName.length());

        return sb.toString();
    }

    @Nullable
    public static Method getDeclaredMethodOrNull(
        @NotNull Class<?> clazz,
        @NotNull String methodName,
        Class<?>... parameters
    ) {
        try {
            return clazz.getDeclaredMethod(methodName, parameters);
        } catch (Exception e) {
            return null;
        }
    }

    @NotNull
    public static Class<?> transformPrimitiveTypeToWrapper(Class<?> c) {
        if (c == boolean.class) {
            return Boolean.class;
        } else if (c == byte.class) {
            return Byte.class;
        } else if (c == short.class) {
            return Short.class;
        } else if (c == int.class) {
            return Integer.class;
        } else if (c == long.class) {
            return Long.class;
        } else if (c == float.class) {
            return Float.class;
        } else if (c == double.class) {
            return Double.class;
        }

        return c;
    }
}
