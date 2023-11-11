package com.mkr.datastore;

import com.mkr.datastore.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class EntityScheme<T> {
    @NotNull
    private final String id;

    @NotNull
    private final Class<T> entityClass;

    @Nullable
    private final Constructor<T> emptyConstructor;
    private final EntitySchemeKey[] keys;

    private final EntitySchemeKeyAccessor[] keyAccessors;

    private final EntityScheme<? extends T> @Nullable [] inheritedSchemes;

    EntityScheme(
        @NotNull String id,
        @NotNull Class<T> entityClass,
        @Nullable Constructor<T> emptyConstructor,
        EntitySchemeKey @NotNull [] keys,
        EntitySchemeKeyAccessor[] accessors,
        EntityScheme<? extends T> @Nullable [] inheritedSchemes
    ) {
        this.id = id;
        this.entityClass = entityClass;
        this.emptyConstructor = emptyConstructor;
        this.keys = keys;
        this.keyAccessors = accessors;
        this.inheritedSchemes = inheritedSchemes;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Class<T> getEntityClass() {
        return entityClass;
    }

    @NotNull
    public EntitySchemeKey[] getDirectKeys() {
        return keys;
    }

    @NotNull
    public Class<? extends T> resolveClassById(@NotNull String id) {
        if (id.isEmpty() || id.equals(this.id)) {
            return entityClass;
        }

        if (inheritedSchemes != null) {
            for (EntityScheme<? extends T> struct : inheritedSchemes) {
                if (struct.id.equals(id)) {
                    return struct.entityClass;
                }
            }
        }

        throw new IllegalArgumentException("Unknown id");
    }

    @NotNull
    public EntitySchemeKey[] getKeysForSubclass(Class<? extends T> subclass) {
        return resolveExactScheme(subclass).keys;
    }

    @Nullable
    public EntitySchemeKey findKeyByName(String name) {
        EntitySchemeKey result = findKeyByNameDirect(name);
        if (result == null && inheritedSchemes != null) {
            for (EntityScheme<?> struct : inheritedSchemes) {
                result = struct.findKeyByNameDirect(name);
                if (result != null) {
                    break;
                }
            }
        }

        return result;
    }

    @Nullable
    private EntitySchemeKey findKeyByNameDirect(@NotNull String name) {
        for (EntitySchemeKey key : keys) {
            if (key.name().equals(name)) {
                return key;
            }
        }

        return null;
    }

    @Nullable
    public EntitySchemeKey findKeyByName(@NotNull Class<? extends T> subclass, @NotNull String name) {
        EntityScheme<? extends T> struct = resolveExactScheme(subclass);

        return struct.findKeyByNameDirect(name);
    }

    @SuppressWarnings("unchecked")
    public Object getKeyValue(@NotNull T entity, @NotNull EntitySchemeKey key) {
        Class<? extends T> exactClass = (Class<? extends T>) entity.getClass();

        return resolveKeyAccessor(exactClass, key).get(entity);
    }

    @SuppressWarnings("unchecked")
    public void setKeyValue(@NotNull T entity, @NotNull EntitySchemeKey key, Object value) {
        Class<? extends T> exactClass = (Class<? extends T>) entity.getClass();

        resolveKeyAccessor(exactClass, key).set(entity, value);
    }

    private EntitySchemeKeyAccessor resolveKeyAccessor(@NotNull Class<? extends T> exactClass, @NotNull EntitySchemeKey key) {
        EntityScheme<?> structToSearch = resolveExactScheme(exactClass);
        int keyIndex = ArrayUtils.indexOf(structToSearch.keys, key);

        if (keyIndex < 0) {
            throw new IllegalArgumentException("Given invalid key");
        }

        return structToSearch.keyAccessors[keyIndex];
    }

    @SuppressWarnings("unchecked")
    private <U extends T> EntityScheme<U> resolveExactScheme(@NotNull Class<U> exactClass) {
        if (entityClass == exactClass) {
            return (EntityScheme<U>) this;
        }

        if (inheritedSchemes != null) {
            for (EntityScheme<? extends T> struct : inheritedSchemes) {
                if (struct.entityClass.equals(exactClass)) {
                    return (EntityScheme<U>) struct;
                }
            }
        }

        throw new IllegalArgumentException("Given unexpected class");
    }

    @NotNull
    public <U extends T> U createInstance(@NotNull Class<U> exactClass) {
        EntityScheme<U> constructingInstScheme = resolveExactScheme(exactClass);
        if (constructingInstScheme == null) {
            throwUnexpectedClass();
        }

        if (constructingInstScheme.emptyConstructor == null) {
            throw new IllegalArgumentException("Cannot instantiate an abstract class");
        }

        try {
            return constructingInstScheme.emptyConstructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            // InvocationTargetException: in most cases, constructor of entity doesn't throw.
            // InstantiationException: cannot happen - class is checked to be instantiatable.
            // IllegalAccessException: cannot happen - constructor is checked to be public.
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static <T> EntityScheme<T> createFromClass(@NotNull Class<T> entityClass) {
        return EntitySchemeCreator.createFromClass(entityClass);
    }

    private static void throwUnexpectedClass() {
        throw new InvalidEntityException("Given unexpected class");
    }
}
