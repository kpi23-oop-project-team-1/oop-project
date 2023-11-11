package com.mkr.datastore;

import com.mkr.datastore.utils.TypeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

final class EntitySchemeCreator {
    private EntitySchemeCreator() {
    }

    @NotNull
    public static <T> EntityScheme<T> createFromClass(@NotNull Class<T> entityClass) {
        validateEntityClass(entityClass);

        EntitySchemeKey[] keys = computeKeysFromClass(entityClass);
        Constructor<T> constructor = tryExtractConstructor(entityClass);
        EntitySchemeKeyAccessor[] accessors = extractAccessors(entityClass, keys);

        String id = "";
        EntityScheme<? extends T>[] inheritedStructs = null;

        var baseModelAnnotation = entityClass.getAnnotation(BaseModel.class);
        if (baseModelAnnotation != null) {
            if (entityClass.isAnnotationPresent(InheritedModel.class)) {
                throw new InvalidEntityException("@InheritedModel annotation cannot on a class that is marked with @BaseModel");
            }

            Class<?>[] inheritedClasses = baseModelAnnotation.inheritedClasses();

            id = baseModelAnnotation.id();
            inheritedStructs = getInheritedSchemes(entityClass, inheritedClasses);
        } else {
            var inheritedModelAnnotation = entityClass.getAnnotation(InheritedModel.class);

            if (inheritedModelAnnotation != null) {
                id = inheritedModelAnnotation.id();
            }
        }

        return new EntityScheme<>(id, entityClass, constructor, keys, accessors, inheritedStructs);
    }

    @NotNull
    private static <T> EntityScheme<T> createFromClassInherited(@NotNull Class<T> entityClass) {
        validateInheritedEntityClass(entityClass);

        Constructor<T> constructor = tryExtractConstructor(entityClass);
        EntitySchemeKey[] keys = computeKeysFromClass(entityClass);
        EntitySchemeKeyAccessor[] accessors = extractAccessors(entityClass, keys);

        var modelAnnotation = entityClass.getAnnotation(InheritedModel.class);
        if (modelAnnotation == null) {
            throw new InvalidEntityException("Expected a @InheritedModel annotation on " + entityClass);
        }

        String id = modelAnnotation.id();

        return new EntityScheme<>(id, entityClass, constructor, keys, accessors, null);
    }

    @SuppressWarnings("unchecked")
    private static <T> EntityScheme<? extends T>[] getInheritedSchemes(
        Class<T> baseEntityClass,
        Class<?>[] inheritedClasses
    ) {
        var result = new EntityScheme<?>[inheritedClasses.length];
        for (int i = 0; i < inheritedClasses.length; i++) {
            Class<?> inheritedClass = inheritedClasses[i];
            if (!baseEntityClass.isAssignableFrom(inheritedClass)) {
                throw new InvalidEntityException(baseEntityClass + " should be a superclass of " + inheritedClass);
            }

            EntityScheme<?> scheme = createFromClassInherited(inheritedClass);
            result[i] = scheme;
        }

        return (EntityScheme<? extends T>[]) result;
    }

    private static EntitySchemeKey @NotNull [] computeKeysFromClass(@NotNull Class<?> keysClass) {
        Field[] fields = TypeUtils.getDeclaredFieldsOfInheritedClasses(keysClass);
        var keys = new EntitySchemeKey[fields.length];
        int keyCount = 0;

        for (Field field : fields) {
            int mods = field.getModifiers();
            if (Modifier.isStatic(mods)) {
                continue;
            }

            Class<?> fieldType = field.getType();
            String name = field.getName();

            keys[keyCount++] = new EntitySchemeKey(fieldType, name);
        }

        if (keyCount != keys.length) {
            keys = Arrays.copyOf(keys, keyCount);
        }

        return keys;
    }

    private static EntitySchemeKeyAccessor[] extractAccessors(Class<?> entityClass, EntitySchemeKey[] keys) {
        var accessors = new EntitySchemeKeyAccessor[keys.length];

        for (int i = 0; i < keys.length; i++) {
            EntitySchemeKey key = keys[i];
            String keyName = key.name();

            String getterName = TypeUtils.createGetterName(keyName);
            String setterName = TypeUtils.createSetterName(keyName);

            Method getter = getAccessor(entityClass, getterName, keyName, "getter");
            Method setter = getAccessor(entityClass, setterName, keyName, "setter", key.valueType());

            validateGetter(key.valueType(), getter);
            validateSetter(key.valueType(), setter);

            accessors[i] = new ReflectionEntitySchemeKeyAccessor(getter, setter);
        }

        return accessors;
    }

    @NotNull
    private static Method getAccessor(
        Class<?> entityClass,
        String methodName,
        String keyName,
        String accessorName,
        Class<?>... params) {
        try {
            return entityClass.getMethod(methodName, params);
        } catch (NoSuchMethodException e) {
            throw new InvalidEntityException("Cannot find a " + accessorName + " for '" + keyName + "' key");
        }
    }

    @Nullable
    private static <T> Constructor<T> tryExtractConstructor(@NotNull Class<T> c) {
        if (Modifier.isAbstract(c.getModifiers())) {
            return null;
        }

        try {
            return c.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new InvalidEntityException("Entity should contain a constructor with no parameters");
        }
    }

    private static void validateEntityClass(@NotNull Class<?> c) {
        int mods = c.getModifiers();
        if (!Modifier.isPublic(mods)) {
            throw new InvalidEntityException("Entity must be public");
        }

        if (c.isPrimitive()) {
            throw new InvalidEntityException("Entity cannot be a primitive type");
        }
    }

    private static void validateInheritedEntityClass(@NotNull Class<?> c) {
        validateEntityClass(c);

        int mods = c.getModifiers();
        if (Modifier.isAbstract(mods)) {
            throw new InvalidEntityException("Inherited entity cannot be abstract");
        }
    }

    private static void validateGetter(@NotNull Class<?> valueClass, @NotNull Method getter) {
        validateAccessor(getter, "Getter");

        Class<?> actualReturnType = getter.getReturnType();
        if (actualReturnType != valueClass) {
            throwUnexpectedGetterReturnType(valueClass, actualReturnType);
        }
    }

    private static void validateSetter(@NotNull Class<?> valueClass, @NotNull Method setter) {
        validateAccessor(setter, "Setter");

        Class<?> actualReturnType = setter.getReturnType();
        if (actualReturnType != void.class) {
            throw new InvalidEntityException("Setter should return void");
        }

        Class<?>[] params = setter.getParameterTypes();
        if (params.length != 1 || params[0] != valueClass) {
            throw new InvalidEntityException("Invalid parameter type of setter");
        }
    }

    private static void validateAccessor(@NotNull Method method, @NotNull String accessorType) {
        int mods = method.getModifiers();
        if (!Modifier.isPublic(mods) || Modifier.isStatic(mods) || Modifier.isAbstract(mods)) {
            throw new InvalidEntityException(accessorType + " should be public instance method");
        }
    }

    private static void throwUnexpectedGetterReturnType(Class<?> expected, Class<?> actual) {
        throw new InvalidEntityException("Unexpected return type of getter: expected=" + expected + ", actual=" + actual);
    }
}
