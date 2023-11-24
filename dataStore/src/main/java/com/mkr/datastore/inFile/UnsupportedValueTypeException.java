package com.mkr.datastore.inFile;

public class UnsupportedValueTypeException extends RuntimeException {
    public UnsupportedValueTypeException() {}

    public UnsupportedValueTypeException(Class<?> valueType) {
        super("This value type is not supported: \"%s\"".formatted(valueType));
    }
}