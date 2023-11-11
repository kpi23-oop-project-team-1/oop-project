package com.mkr.datastore;

public interface EntitySchemeKeyAccessor {
    Object get(Object obj);
    void set(Object obj, Object value);
}
