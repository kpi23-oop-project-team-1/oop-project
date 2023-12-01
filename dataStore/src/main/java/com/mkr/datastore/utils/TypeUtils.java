package com.mkr.datastore.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class TypeUtils {
    @NotNull
    public static String createGetterName(@NotNull String valueName) {
        return withPrefixAndCapitalizedName(valueName, "get");
    }

    @NotNull
    public static String createSetterName(@NotNull String valueName) {
        return withPrefixAndCapitalizedName(valueName, "set");
    }

    @NotNull
    private static String withPrefixAndCapitalizedName(@NotNull String valueName, @NotNull String prefix) {
        if (valueName.isEmpty()) {
            return prefix;
        }

        // valueName => [prefix][ValueName]
        // "number" => [prefix]Number
        var sb = new StringBuilder();
        sb.append(prefix);
        sb.appendCodePoint(Character.toUpperCase(valueName.codePointAt(0)));
        sb.append(valueName,1, valueName.length());

        return sb.toString();
    }

    @NotNull
    public static Field[] getDeclaredFieldsOfInheritedClasses(@NotNull Class<?> c) {
        if (c.getSuperclass() == Object.class) {
            return c.getDeclaredFields();
        }

        var result = new ArrayList<Field>();

        Class<?> i = c;
        while (i != null && i != Object.class) {
            Collections.addAll(result, i.getDeclaredFields());
            i = i.getSuperclass();
        }

        return result.toArray(new Field[0]);
    }

    public static Object invokeMethodSilent(@NotNull Object obj, @NotNull Method method, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object instantiateEnumByString(Class<?> clazz, String stringValue) {
        try {
            Method method = clazz.getDeclaredMethod("values");

            Object[] values = (Object[]) method.invoke(null);

            for (var value : values) {
                if (!Objects.equals(value.toString(), stringValue)) continue;
                return value;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
