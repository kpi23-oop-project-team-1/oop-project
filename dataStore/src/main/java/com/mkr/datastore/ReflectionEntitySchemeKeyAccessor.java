package com.mkr.datastore;

import com.mkr.datastore.utils.TypeUtils;

import java.lang.reflect.Method;

public final class ReflectionEntitySchemeKeyAccessor implements EntitySchemeKeyAccessor {
    private final Method getter;
    private final Method setter;

    public ReflectionEntitySchemeKeyAccessor(Method getter, Method setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Object get(Object obj) {
        return TypeUtils.invokeMethodSilent(obj, getter);
    }

    @Override
    public void set(Object obj, Object value) {
        TypeUtils.invokeMethodSilent(obj, setter, value);
    }
}
