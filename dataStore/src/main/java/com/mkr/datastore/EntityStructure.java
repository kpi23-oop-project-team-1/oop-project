package com.mkr.datastore;

import com.mkr.datastore.utils.ArrayUtils;
import com.mkr.datastore.utils.TypeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

public final class EntityStructure<T> {
    final Class<T> entityClass;

    final EntityStructureKey<?>[] keys;
    final Method[] getters;

    public EntityStructure(Class<T> entityClass, EntityStructureKey<?>[] keys) {
        this.entityClass = entityClass;
        this.keys = keys;

        getters = extractGetters(entityClass, keys);
    }

    public <K> K getKeyValue(T entity, EntityStructureKey<K> key) {
        return getKeyValueViaGetter(entity, key, resolveKeyGetter(key));
    }

    public<K extends Comparable<K>> Comparator<T> createKeyComparator(EntityStructureKey<K> key) {
        Method getter = resolveKeyGetter(key);

        return Comparator.comparing(entity -> getKeyValueViaGetter(entity, key, getter));
    }

    @NotNull
    public <K> Function<T, K> getKeyMapper(@NotNull EntityStructureKey<K> key) {
        Method getter = resolveKeyGetter(key);

        return entity -> getKeyValueViaGetter(entity, key, getter);
    }

    private Method resolveKeyGetter(EntityStructureKey<?> key) {
        int keyIndex = ArrayUtils.indexOf(keys, key);
        if (keyIndex < 0) {
            throw new IllegalArgumentException("Given key is not in the structure");
        }

        return getters[keyIndex];
    }

    @SuppressWarnings("unchecked")
    private<K> K getKeyValueViaGetter(T entity, @NotNull EntityStructureKey<K> key, @NotNull Method getter) {
        Class<K> keyValueType = key.valueType();
        Class<?> wrapperType = TypeUtils.transformPrimitiveTypeToWrapper(keyValueType);

        try {
            return (K)wrapperType.cast(getter.invoke(entity));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public static<T> EntityStructure<T> create(Class<T> entityClass, Class<?> keysClass) {
        EntityStructureKey<?>[] keys = getKeysFromHolderClass(keysClass);

        return new EntityStructure<>(entityClass, keys);
    }

    private static EntityStructureKey<?>[] getKeysFromHolderClass(Class<?> keysClass) {
        Field[] fields = keysClass.getFields();
        var keys = new EntityStructureKey<?>[fields.length];
        int keyCount = 0;

        for (Field field : fields) {
            int mods = field.getModifiers();
            if (!Modifier.isStatic(mods)) {
                continue;
            }

            Class<?> fieldType = field.getType();
            if (!fieldType.equals(EntityStructureKey.class)) {
                continue;
            }

            try {
                var key = (EntityStructureKey<?>)field.get(null);

                keys[keyCount++] = key;
            } catch (IllegalAccessException e) {
                // We query only public fields, so this exception should not occur.
                throw new RuntimeException(e);
            }
        }

        if (keyCount != keys.length) {
            // Unlikely that class, which is purposed that contain only keys, will contain non-key fields.
            keys = Arrays.copyOf(keys, keyCount);
        }

        return keys;
    }

    private static Method[] extractGetters(Class<?> entityClass, EntityStructureKey<?>[] keys) {
        Method[] methods = new Method[keys.length];

        for (int i = 0; i < keys.length; i++) {
            EntityStructureKey<?> key = keys[i];
            String keyName = key.name();

            Method getter = deduceGetter(entityClass, key.name());
            if (getter == null) {
                throw new IllegalStateException("Cannot find a getter for '" + keyName + "' key");
            }

            validateGetter(entityClass, key.valueType(), getter);

            methods[i] = getter;
        }

        return methods;
    }

    @Nullable
    private static Method deduceGetter(@NotNull Class<?> entityClass, @NotNull String name) {
        String getterName = TypeUtils.createGetterName(name);

        // Getter should have no arguments.
        return TypeUtils.getDeclaredMethodOrNull(entityClass, getterName);
    }

    private static void validateGetter(
        @NotNull Class<?> entityClass,
        @NotNull Class<?> valueClass,
        @NotNull Method getter
    ) {
        Class<?> actualReturnType = getter.getReturnType();
        if (!actualReturnType.equals(valueClass)) {
            throwUnexpectedGetterReturnType(valueClass, actualReturnType);
        }

        Class<?> declaringClass = getter.getDeclaringClass();
        if (!declaringClass.equals(entityClass)) {
            throw new IllegalStateException("Unexpected getter's declaring class");
        }

        int mods = getter.getModifiers();
        if (!Modifier.isPublic(mods)) {
            throw new IllegalStateException("Getter is expected to be public");
        }

        if (Modifier.isStatic(mods)) {
            throw new IllegalStateException("Getter is expected to be non-static");
        }

        if (Modifier.isAbstract(mods)) {
            throw new IllegalStateException("Getter is expected to be non-abstract");
        }
    }

    private static void throwUnexpectedGetterReturnType(Class<?> expected, Class<?> actual) {
        throw new IllegalStateException("Unexpected return type of getter: expected=" + expected + ", actual=" + actual);
    }
}
